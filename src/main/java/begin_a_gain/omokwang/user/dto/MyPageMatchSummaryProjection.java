package begin_a_gain.omokwang.user.dto;

public interface MyPageMatchSummaryProjection {
    Long getMatchId();

    String getMatchName();

    Integer getParticipantDays();

    Integer getComboCount();

    Integer getParticipantNumbers();

    String getDayOfWeeks();
}
