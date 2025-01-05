package begin_a_gain.omokwang.user.service;

import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.util.Optional;
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

    public void updateRefreshToken(User user) {
        Long socialId = user.getSocialId();
        String refreshToken = user.getRefreshToken();
        userRepository.updateRefreshToken(socialId, refreshToken);
    }

    public boolean isSignUpComplete(long socialId) {
        return userRepository.existsNicknameBySocialId(socialId);
    }

    public Optional<User> findBySocialIdAndPlatform(Long socialId, String platform) {
        return userRepository.findBySocialIdAndPlatform(socialId, platform);
    }

}
