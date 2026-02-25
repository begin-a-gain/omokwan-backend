package begin_a_gain.omokwang.match_detail.application;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.exception.CustomException;
import begin_a_gain.omokwang.common.exception.ErrorCode;
import begin_a_gain.omokwang.match.domain.MatchInfo;
import begin_a_gain.omokwang.match.repository.MatchRepository;
import begin_a_gain.omokwang.match.repository.MatchStatusRepository;
import begin_a_gain.omokwang.match_detail.domain.MatchParticipant;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchRequest;
import begin_a_gain.omokwang.match_detail.dto.JoinMatchResponse;
import begin_a_gain.omokwang.match_detail.dto.KickUserResponse;
import begin_a_gain.omokwang.match_detail.dto.LeaveMatchResponse;
import begin_a_gain.omokwang.match_detail.dto.MatchParticipantsResponse;
import begin_a_gain.omokwang.match_detail.dto.ParticipantInfo;
import begin_a_gain.omokwang.match_detail.dto.UserProfileResponse;
import begin_a_gain.omokwang.match_detail.repository.MatchParticipantRepository;
import begin_a_gain.omokwang.notification.domain.NotificationEvent;
import begin_a_gain.omokwang.notification.domain.NotificationRecipient;
import begin_a_gain.omokwang.notification.domain.NotificationType;
import begin_a_gain.omokwang.notification.repository.NotificationEventRepository;
import begin_a_gain.omokwang.notification.repository.NotificationRecipientRepository;
import begin_a_gain.omokwang.user.dto.User;
import begin_a_gain.omokwang.user.repository.UserRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MatchDetailService {

    private final MatchRepository matchRepository;
    private final MatchParticipantRepository matchParticipantRepository;
    private final MatchStatusRepository matchStatusRepository;
    private final UserRepository userRepository;
    private final NotificationEventRepository notificationEventRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final Clock clock;

    @Transactional
    public JoinMatchResponse joinMatch(Long matchId, JoinMatchRequest request) {
        var match = matchRepository.findById(matchId)
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));
        checkMatchPassword(request, match);
        if (!hasJoinedMatch(matchId)) {
            checkMatchCapacityFull(matchId, match.getMaxParticipants());
            var matchParticipant = convertToMatchParticipant(match);
            matchParticipantRepository.save(matchParticipant);
            notifyMemberJoined(matchId, matchParticipant.getUser(), match.getName());
        }
        return JoinMatchResponse.builder()
                .matchId(matchId)
                .build();
    }

    private void checkMatchPassword(JoinMatchRequest request, MatchInfo match) {
        if (!match.isPublic()) {
            var inputPassword = request.getPassword();
            boolean passwordMatches = Objects.equals(inputPassword, match.getPassword());
            if (!passwordMatches) {
                throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
            }
        }
    }

    private MatchParticipant convertToMatchParticipant(MatchInfo match) {
        User participant = getCurrentUser();
        return MatchParticipant.builder()
                .match(match)
                .user(participant)
                .joinOrder(getJoinOrder(match.getId()))
                .build();
    }

    private User getCurrentUser() {
        var userId = getCurrentUserId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
    }

    protected Long getCurrentUserId() {
        return SecurityUtil.getCurrentUserId();
    }

    private int getJoinOrder(Long matchId) {
        return matchParticipantRepository.findMaxJoinOrderByMatchId(matchId) + 1;
    }

    private boolean hasJoinedMatch(Long matchId) {
        var matchUserIds = getMatchUserIds(matchId);
        var currentUserId = getCurrentUser().getId();
        return matchUserIds.contains(currentUserId);
    }

    private List<Long> getMatchUserIds(Long matchId) {
        var users = matchParticipantRepository.findUsersByMatchId(matchId);
        return users.stream().map(User::getId).toList();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getUserProfileByMatchId(Long matchId, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + userId));
        var hostId = getHostId(matchId);
        return UserProfileResponse.builder()
                .nickName(user.getNickname())
                .combo(matchStatusRepository.comboNumberByMatchIdAndUserId(matchId, userId))
                .participantNumbers(matchStatusRepository.participantNumberByMatchIdAndUserId(matchId, userId))
                .participantDays(getParticipantDays(matchId, userId))
                .host(hostId.equals(userId))
                .build();
    }

    private Long getHostId(Long matchId) {
        var user = matchParticipantRepository.findByMatchIdAndIsHostTrue(matchId)
                .orElseThrow(() -> new IllegalArgumentException("Host not found: " + matchId));
        return user.getUser().getId();
    }

    private long getParticipantDays(Long matchId, Long userId) {
        var matchInfo = matchParticipantRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Match not found with ID: " + matchId));
        var joinDay = matchInfo.getJoinDate();
        return ChronoUnit.DAYS.between(joinDay, LocalDate.now(clock)) + 1;
    }

    private void checkMatchCapacityFull(Long matchId, int maxParticipants) {
        var currentParticipants = matchParticipantRepository.findUsersByMatchId(matchId).size();
        if (maxParticipants == currentParticipants) {
            throw new CustomException(ErrorCode.MATCH_CAPACITY_FULL);
        }
    }

    @Transactional(readOnly = true)
    public MatchParticipantsResponse getParticipants(Long matchId) {

        var users = matchParticipantRepository.findUsersByMatchId(matchId);
        var userInfo = users.stream()
                .map(u -> ParticipantInfo.builder()
                        .userId(u.getId())
                        .nickname(u.getNickname())
                        .combo(matchStatusRepository.comboNumberByMatchIdAndUserId(matchId, u.getId()))
                        .participantNumbers(
                                matchStatusRepository.participantNumberByMatchIdAndUserId(matchId, u.getId()))
                        .participantDays(getParticipantDays(matchId, u.getId()))
                        .build())
                .toList();
        return MatchParticipantsResponse.builder()
                .userInfo(userInfo)
                .build();
    }


    @Transactional
    public KickUserResponse kickUserFromMatch(Long matchId, Long userId) {
        requireHost(matchId);

        var matchParticipant = matchParticipantRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        matchParticipant.kickNow(clock);
        notifyMemberLeft(matchId, matchParticipant.getUser(), matchParticipant.getMatch().getName());

        return KickUserResponse.builder()
                .userId(userId)
                .build();
    }

    private void requireHost(Long matchId) {
        var currentUserId = getCurrentUser().getId();

        var hostUserId = matchParticipantRepository
                .findByMatchIdAndIsHostTrue(matchId)
                .map(p -> p.getUser().getId())
                .orElseThrow(() -> new CustomException(ErrorCode.MATCH_NOT_FOUND));

        if (!hostUserId.equals(currentUserId)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
    }

    @Transactional
    public LeaveMatchResponse leaveMatch(Long matchId) {
        var currentUser = getCurrentUser();
        var userId = currentUser.getId();
        var matchParticipant = matchParticipantRepository.findByMatchIdAndUserId(matchId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        matchParticipant.leaveNow(clock);
        notifyMemberLeft(matchId, currentUser, matchParticipant.getMatch().getName());

        return LeaveMatchResponse.builder()
                .userId(userId)
                .build();
    }

    private void notifyMemberLeft(Long matchId, User leftUser, String matchName) {
        var recipientUserIds = matchParticipantRepository.findUsersByMatchId(matchId).stream()
                .map(User::getId)
                .filter(id -> !id.equals(leftUser.getId()))
                .toList();

        if (recipientUserIds.isEmpty()) {
            return;
        }

        var occurredAt = OffsetDateTime.ofInstant(Instant.now(clock), ZoneOffset.UTC);
        var event = NotificationEvent.builder()
                .type(NotificationType.MEMBER_LEFT)
                .matchId(matchId)
                .actorUserId(leftUser.getId())
                .matchNameSnapshot(matchName)
                .actorNicknameSnapshot(leftUser.getNickname())
                .occurredAt(occurredAt)
                .build();
        var savedEvent = notificationEventRepository.save(event);

        var recipients = recipientUserIds.stream()
                .map(recipientUserId -> NotificationRecipient.builder()
                        .notificationEvent(savedEvent)
                        .recipientUserId(recipientUserId)
                        .isRead(false)
                        .build())
                .toList();
        notificationRecipientRepository.saveAll(recipients);
    }

    private void notifyMemberJoined(Long matchId, User joinedUser, String matchName) {
        var recipientUserIds = matchParticipantRepository.findUsersByMatchId(matchId).stream()
                .map(User::getId)
                .filter(id -> !id.equals(joinedUser.getId()))
                .toList();

        if (recipientUserIds.isEmpty()) {
            return;
        }

        var occurredAt = OffsetDateTime.ofInstant(Instant.now(clock), ZoneOffset.UTC);
        var event = NotificationEvent.builder()
                .type(NotificationType.MATCH_JOINED)
                .matchId(matchId)
                .actorUserId(joinedUser.getId())
                .matchNameSnapshot(matchName)
                .actorNicknameSnapshot(joinedUser.getNickname())
                .occurredAt(occurredAt)
                .build();
        var savedEvent = notificationEventRepository.save(event);

        var recipients = recipientUserIds.stream()
                .map(recipientUserId -> NotificationRecipient.builder()
                        .notificationEvent(savedEvent)
                        .recipientUserId(recipientUserId)
                        .isRead(false)
                        .build())
                .toList();
        notificationRecipientRepository.saveAll(recipients);
    }


}
