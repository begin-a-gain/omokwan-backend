package begin_a_gain.omokwang.match_detail.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchRequest;
import begin_a_gain.omokwang.match_detail.dto.UserProfileResponse;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchDetailService {

    private final MatchRepository matchRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchStatusRepository matchStatusRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void joinMatch(Long matchId, JoinMatchRequest request) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.DAEGUK_NOT_FOUND));
        checkMatchPassword(request, match);
        var matchParticipant = convertToMatchParticipant(match);
        matchParticipantRepository.save(matchParticipant);
    }

    private void checkMatchPassword(JoinMatchRequest request, MatchInfo match) {
        if (!match.isPublic()) {
            var inputPassword = request.getPassword();
            var encodedPassword = match.getPassword();
            boolean result = passwordEncoder.matches(inputPassword, encodedPassword);
            if (!result) {
                throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
            }
        }
    }

    private MatchParticipant convertToMatchParticipant(MatchInfo match) {
        User participant = getParticipant();
        return MatchParticipant.builder()
                .match(match)
                .user(participant)
                .build();
    }

    private User getParticipant() {
        var socialId = SecurityUtil.getCurrentUserSocialId();
        return userRepository.findBySocialId(socialId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + socialId));
    }

    public UserProfileResponse getUserProfileByMatchId(Long matchId, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));

        return UserProfileResponse.builder()
                .nickName(user.getNickname())
                .combo(matchStatusRepository.comboNumberByMatchIdAndUserId(matchId, userId))
                .participantNumbers(matchStatusRepository.participantNumberByMatchIdAndUserId(matchId, userId))
                .participantDays(getParticipantDays(matchId, userId))
                .build();
    }

    private long getParticipantDays(Long matchId, Long userId) {
        var matchInfo = matchParticipantRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));
        var joinDay = matchInfo.getJoinDate();
        var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        return ChronoUnit.DAYS.between(joinDay, today) + 1;
    }

}
