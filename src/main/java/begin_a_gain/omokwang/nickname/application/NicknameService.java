package begin_a_gain.omokwang.nickname.application;

import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.nickname.domain.NicknameUpdateDto;
import begin_a_gain.omokwang.nickname.ui.NicknameValidateResponse;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NicknameService {
    private static final String INVALID_NICKNAME_MESSAGE = "Invalid nickname: Must be 2-10 characters long and cannot include special characters.";
    private static final String NICKNAME_TAKEN_MESSAGE = "Nickname already taken.";

    private final UserRepository userRepository;

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

    public NicknameValidateResponse validateNickname(String nickname) {
        if (isInvalidNickname(nickname)) {
            return NicknameValidateResponse.builder()
                    .isValid(false)
                    .isDuplicated(false)
                    .build();
        }

        if (isNicknameTaken(nickname)) {
            return NicknameValidateResponse.builder()
                    .isValid(true)
                    .isDuplicated(true)
                    .build();
        }

        return NicknameValidateResponse.builder()
                .isValid(true)
                .isDuplicated(false)
                .build();
    }

}
