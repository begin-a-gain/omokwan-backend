package begin_a_gain.omokwang.match_detail.application;

import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.repository.MatchDayRepository;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match_detail.dto.MatchSettingResponse;
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


    @Transactional(readOnly = true)
    public MatchSettingResponse getSettingMatch(Long matchId) {

        var matchInfo = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

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

    private int getOngoingDays(LocalDate createDate) {
        var today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        long daysBetween = ChronoUnit.DAYS.between(createDate, today) + 1;
        return (int) daysBetween;
    }
}
