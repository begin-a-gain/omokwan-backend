package begin_a_gain.omokwang.match_detail.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match_detail.dto.MatchSettingResponse;
import begin_a_gain.omokwang.match_detail.dto.MatchSettingUpdateRequest;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchSettingService {

    private final MatchRepository matchRepository;
    private final MatchDayRepository matchDayRepository;
    private final MatchParticipantRepository matchParticipantRepository;


    @Transactional(readOnly = true)
    public MatchSettingResponse getSettingMatch(Long matchId) {

        var matchInfo = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        return toResponse(matchInfo, matchId);
    }

    @Transactional
    public void updateSettingMatch(Long matchId, MatchSettingUpdateRequest request) {
        requireHost(matchId);
        var matchInfo = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        validatePasswordForPrivateSwitch(matchInfo.isPublic(), request.isPublic(), request.password());

        matchInfo.updateSettings(
                request.name(),
                request.maxParticipants(),
                request.category(),
                request.isPublic(),
                request.password()
        );
    }

    private void requireHost(Long matchId) {
        var currentUserId = getCurrentUserId();
        var hostUserId = matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId)
                .map(participant -> participant.getUser().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        if (!hostUserId.equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    protected Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }

    private int getOngoingDays(LocalDate createDate) {
        var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        long daysBetween = ChronoUnit.DAYS.between(createDate, today) + 1;
        return (int) daysBetween;
    }

    private MatchSettingResponse toResponse(MatchInfo matchInfo, Long matchId) {
        return MatchSettingResponse.builder()
                .name(matchInfo.getName())
                .ongoingDays(getOngoingDays(matchInfo.getCreateDate()))
                .matchCode(matchInfo.getMatchCode())
                .repeatDayTypes(matchDayRepository.findDayOfWeeksByMatchId(matchId))
                .maxParticipants(matchInfo.getMaxParticipants())
                .category(matchInfo.getCategory())
                .publicMatch(matchInfo.isPublic())
                .password(matchInfo.getPassword())
                .build();
    }

    private void validatePasswordForPrivateSwitch(boolean currentPublic, boolean nextPublic, String password) {
        if (currentPublic && !nextPublic && (password == null || password.isBlank())) {
            throw new IllegalArgumentException("Password is required when changing match to private.");
        }
    }
}
