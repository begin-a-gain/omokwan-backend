package begin_a_gain.omokwang.user.service;

import begin_a_gain.omokwang.user.dto.UserDto;
import begin_a_gain.omokwang.user.mapper.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public void save(UserDto userDto) {
        userRepository.save(userDto);
    }

    public UserDto findById(Long id) {
        return userRepository.findById(id);
    }

    public UserDto findByRefreshToken(String refreshToken) {
        return userRepository.findByRefreshToken(refreshToken);
    }

    public void updateRefreshToken(UserDto userDto) {
        userRepository.updateRefreshToken(userDto);
    }
}
