package begin_a_gain.omokwang.daeguk_detail.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.daeguk.domain.Daeguk;
import begin_a_gain.omokwang.daeguk.repository.DaegukRepository;
import begin_a_gain.omokwang.daeguk_detail.domain.DaegukParticipant;
import begin_a_gain.omokwang.daeguk_detail.dto.JoinDaegukRequest;
import begin_a_gain.omokwang.daeguk_detail.repository.DaegukParticipantRepository;
import begin_a_gain.omokwang.exception.CustomException;
import begin_a_gain.omokwang.exception.ErrorCode;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DaegukDetailService {

    private final DaegukRepository daegukRepository;
    private final DaegukParticipantRepository daegukParticipantRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void joinDaeguk(Long daegukId, JoinDaegukRequest request) {
        var daeguk = daegukRepository.findById(daegukId)
                .orElseThrow(() -> new CustomException(ErrorCode.DAEGUK_NOT_FOUND));
        checkDaegukPassword(request, daeguk);
        var daegukParticipant = convertToDaegukParticipant(daeguk);
        daegukParticipantRepository.save(daegukParticipant);
    }

    private void checkDaegukPassword(JoinDaegukRequest request, Daeguk daeguk) {
        if (!daeguk.isPublic()) {
            var inputPassword = request.getPassword();
            var encodedPassword = daeguk.getPassword();
            boolean result = passwordEncoder.matches(inputPassword, encodedPassword);
            if (!result) {
                throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
            }
        }
    }

    private DaegukParticipant convertToDaegukParticipant(Daeguk daeguk) {
        User participant = getParticipant();
        return DaegukParticipant.builder()
                .daeguk(daeguk)
                .user(participant)
                .build();
    }

    private User getParticipant() {
        var socialId = SecurityUtil.getCurrentUserSocialId();
        return userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
    }

}
