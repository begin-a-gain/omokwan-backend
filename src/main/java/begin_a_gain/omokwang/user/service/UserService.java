package begin_a_gain.omokwang.user.service;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.user.dto.DeletionSurvey;
import begin_a_gain.omokwang.user.dto.DeletionSurveyRequest;
import begin_a_gain.omokwang.user.dto.MyPageMatchSummaryProjection;
import begin_a_gain.omokwang.user.dto.MyPageMatchSummaryResponse;
import begin_a_gain.omokwang.user.dto.MyPageResponse;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.DeletionSurveyRepository;
import begin_a_gain.omokwang.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final DeletionSurveyRepository deletionSurveyRepository;

    public User save(User user) {
        return userRepository.save(user);
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

    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setRefreshToken(refreshToken);
    }

    public boolean isSignUpComplete(long userId) {
        return userRepository.existsByIdAndNicknameIsNotNull(userId);
    }

    public Optional<User> findBySocialIdAndPlatform(Long socialId, String platform) {
        return userRepository.findBySocialIdAndPlatform(socialId, platform);
    }

    @Transactional
    public void deleteUser(long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        matchParticipantRepository.deleteByUserId(user.getId());
        updateUserInfo(user);
    }

    private void updateUserInfo(User user) {
        var maskedEmail = getMaskedEmail(user);
        user.setEmail(maskedEmail);
        user.setSocialId(null);
        user.setDeleted(true);
    }

    private String getMaskedEmail(User user) {

        var email = user.getEmail();
        return "[DELETED]_" + UUID.randomUUID() + "_" + email;
    }

    @Transactional
    public void deletionSurvey(DeletionSurveyRequest request) {
        long userId = SecurityUtil.getCurrentUserId();
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        DeletionSurvey survey = DeletionSurvey.builder()
                .userId(user.getId())
                .reasons(request.reasons())
                .otherReason(request.otherReason())
                .build();

        deletionSurveyRepository.save(survey);
    }

    @Transactional
    public MyPageResponse getMyPage(Long userId) {
        validateMyPageAccess(userId);

        var user = findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
        var inProgressMatches = matchParticipantRepository.findInProgressMatchSummaries(userId).stream()
                .map(this::toMyPageMatchSummary)
                .toList();
        var completedMatches = matchParticipantRepository.findCompletedMatchSummaries(userId).stream()
                .map(this::toMyPageMatchSummary)
                .toList();

        return MyPageResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .inProgressMatchCount(matchParticipantRepository.countByUser_IdAndLeaveDateIsNull(userId))
                .completedMatchCount(matchParticipantRepository.countByUser_IdAndLeaveDateIsNotNull(userId))
                .inProgressMatches(inProgressMatches)
                .completedMatches(completedMatches)
                .build();
    }

    private void validateMyPageAccess(Long requestedUserId) {
        var currentUserId = SecurityUtil.getCurrentUserId();
        if (!Long.valueOf(currentUserId).equals(requestedUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    private MyPageMatchSummaryResponse toMyPageMatchSummary(MyPageMatchSummaryProjection projection) {
        var dayOfWeeks = parseDayOfWeeks(projection.getDayOfWeeks());

        return MyPageMatchSummaryResponse.builder()
                .matchId(projection.getMatchId())
                .matchName(projection.getMatchName())
                .participantDays(getOrZero(projection.getParticipantDays()))
                .comboCount(getOrZero(projection.getComboCount()))
                .participantNumbers(getOrZero(projection.getParticipantNumbers()))
                .dayOfWeeks(dayOfWeeks)
                .build();
    }

    private List<Integer> parseDayOfWeeks(String dayOfWeeks) {
        if (dayOfWeeks == null || dayOfWeeks.isBlank()) {
            return Collections.emptyList();
        }

        return java.util.Arrays.stream(dayOfWeeks.split(","))
                .map(String::trim)
                .filter(value -> !value.isBlank())
                .map(Integer::parseInt)
                .toList();
    }

    private int getOrZero(Integer value) {
        return value == null ? 0 : value;
    }

}
