package begin_a_gain.omokwang.user.service;

import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
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

    public void updateRefreshToken(User user) {
        Long socialId = user.getSocialId();
        String refreshToken = user.getRefreshToken();
        userRepository.updateRefreshToken(socialId, refreshToken);
    }

    public Optional<User> findBySocialIdAndPlatform(Long socialId, String platform) {
        return userRepository.findBySocialIdAndPlatform(socialId, platform);
    }

    public boolean isNicknameTaken(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    public boolean isValidNickname(String nickname) {
        String nicknamePattern = "^[a-zA-Z0-9가-힣]{2,10}$";
        return nickname.matches(nicknamePattern);
    }
}
