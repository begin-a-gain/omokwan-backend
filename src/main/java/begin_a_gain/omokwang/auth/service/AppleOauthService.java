package begin_a_gain.omokwang.auth.service;

import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class AppleOauthService {

    private static final String APPLE_PLATFORM = "apple";
    private static final String APPLE_ISSUER = "https://appleid.apple.com";
    private static final String APPLE_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Value("${apple.client-id:}")
    private String appleClientId;

    public User getUserProfileByIdentityToken(String identityToken) {
        Claims claims = parseAndValidateIdentityToken(identityToken);

        String sub = claims.getSubject();
        if (sub == null || sub.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        String email = claims.get("email", String.class);
        Long socialId = toAppleSocialId(sub);

        User newUser = User.builder()
                .socialId(socialId)
                .email(email == null ? "" : email)
                .platform(APPLE_PLATFORM)
                .build();

        var existingUser = userService.findBySocialIdAndPlatform(socialId, APPLE_PLATFORM);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        return userService.save(newUser);
    }

    private Claims parseAndValidateIdentityToken(String identityToken) {
        try {
            if (appleClientId == null || appleClientId.isBlank()) {
                throw new CustomException(ErrorCode.BAD_REQUEST);
            }

            String[] tokenParts = identityToken.split("\\.");
            if (tokenParts.length != 3) {
                throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
            }

            String headerJson = new String(Base64.getUrlDecoder().decode(tokenParts[0]), StandardCharsets.UTF_8);
            Map<String, Object> header = objectMapper.readValue(headerJson, new TypeReference<>() {
            });

            String kid = (String) header.get("kid");
            String alg = (String) header.get("alg");
            PublicKey publicKey = getApplePublicKey(kid, alg);

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(publicKey)
                    .build()
                    .parseClaimsJws(identityToken)
                    .getBody();

            validateIssuer(claims);
            validateAudience(claims);

            return claims;
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    private PublicKey getApplePublicKey(String kid, String alg) throws Exception {
        Map<String, Object> keyResponse = WebClient.create()
                .get()
                .uri(APPLE_KEYS_URL)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
                })
                .block();

        if (keyResponse == null || !keyResponse.containsKey("keys")) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> keys = (List<Map<String, Object>>) keyResponse.get("keys");

        Map<String, Object> matchedKey = keys.stream()
                .filter(key -> kid.equals(key.get("kid")) && alg.equals(key.get("alg")))
                .findFirst()
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_ACCESS_TOKEN));

        String n = (String) matchedKey.get("n");
        String e = (String) matchedKey.get("e");

        byte[] modulusBytes = Base64.getUrlDecoder().decode(n);
        byte[] exponentBytes = Base64.getUrlDecoder().decode(e);

        RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
                new BigInteger(1, modulusBytes),
                new BigInteger(1, exponentBytes)
        );

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private void validateIssuer(Claims claims) {
        Object issuer = claims.get("iss");
        if (!APPLE_ISSUER.equals(issuer)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    private void validateAudience(Claims claims) {
        Object audienceClaim = claims.get("aud");

        boolean valid = false;
        if (audienceClaim instanceof String audience) {
            valid = appleClientId.equals(audience);
        } else if (audienceClaim instanceof List<?> audienceList) {
            valid = audienceList.stream().anyMatch(appleClientId::equals);
        }

        if (!valid) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }
    }

    private Long toAppleSocialId(String sub) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sub.getBytes(StandardCharsets.UTF_8));
            long value = ByteBuffer.wrap(hash, 0, Long.BYTES).getLong();
            long positive = value == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(value);
            return -positive;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }
}
