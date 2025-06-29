package begin_a_gain.omokwang.user.service;

import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void save(User user) {
        userRepository.save(user);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findBySocialId(Long id) {
        return userRepository.findBySocialId(id);
    }

    public User findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    @Transactional
    public void updateRefreshToken(Long id, String refreshToken) {
        User user = userRepository.findBySocialId(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRefreshToken(refreshToken);
    }

    public boolean isSignUpComplete(long socialId) {
        return userRepository.existsNicknameBySocialId(socialId);
    }

    public Optional<User> findBySocialIdAndPlatform(Long socialId, String platform) {
        return userRepository.findBySocialIdAndPlatform(socialId, platform);
    }

    @Transactional
    public void deleteUser(long socialId) {
        var user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        var maskedEmail = getMaskedEmail(user);
        user.setEmail(maskedEmail);
        user.setSocialId(null);
        user.setDeleted(true);
    }

    private String getMaskedEmail(User user) {

        var email = user.getEmail();
        return "[DELETED]_" + UUID.randomUUID() + "_" + email;
    }
}
