package begin_a_gain.omokwang.user.service;

import begin_a_gain.omokwang.exception.CustomException;
import begin_a_gain.omokwang.exception.ErrorCode;
import begin_a_gain.omokwang.user.dto.NicknameUpdateDto;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private static final String INVALID_NICKNAME_MESSAGE = "Invalid nickname: Must be 2-10 characters long and cannot include special characters.";
    private static final String NICKNAME_TAKEN_MESSAGE = "Nickname already taken.";

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

    public void updateRefreshToken(User user) {
        Long socialId = user.getSocialId();
        String refreshToken = user.getRefreshToken();
        userRepository.updateRefreshToken(socialId, refreshToken);
    }

    public Optional<User> findBySocialIdAndPlatform(Long socialId, String platform) {
        return userRepository.findBySocialIdAndPlatform(socialId, platform);
    }

    @Transactional(readOnly = true)
    public boolean isNicknameTaken(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isInvalidNickname(String nickname) {
        String nicknamePattern = "^[a-zA-Z0-9가-힣]{2,10}$";
        return !nickname.matches(nicknamePattern);
    }

    @Transactional
    public void updateNickname(NicknameUpdateDto nicknameUpdateParam) {
        long socialId = nicknameUpdateParam.getSocialId();
        User user = userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        String nickname = nicknameUpdateParam.getNickname();
        user.setNickname(nickname);

        userRepository.save(user);
    }

    public Optional<String> validateNickname(String nickname) {
        if (isInvalidNickname(nickname)) {
            return Optional.of(INVALID_NICKNAME_MESSAGE);
        }

        if (isNicknameTaken(nickname)) {
            return Optional.of(NICKNAME_TAKEN_MESSAGE);
        }

        return Optional.empty(); // 닉네임이 유효하고 사용 가능함을 의미
    }
}
