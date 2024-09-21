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

    public User findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    public void updateRefreshToken(User user) {
        Long id = user.getId();
        String refreshToken = user.getRefreshToken();
        userRepository.updateRefreshToken(id, refreshToken);
    }
}
