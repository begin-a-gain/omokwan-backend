package begin_a_gain.omokwang.notification.controller;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.common.response.ErrorResponse;
import begin_a_gain.omokwang.notification.application.NotificationService;
import begin_a_gain.omokwang.notification.dto.NotificationListResponse;
import begin_a_gain.omokwang.notification.dto.NotificationReadRequest;
import begin_a_gain.omokwang.notification.dto.NotificationUnreadStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Notification", description = "Notification API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "전체/안읽음 알림 목록 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "성공"

            ),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/notifications")
    public ResponseEntity<CommonResponse<NotificationListResponse>> getNotifications(
            @Parameter(description = "필터(all, unread)", example = "all")
            @RequestParam(name = "filter", defaultValue = "all") String filter
    ) {
        var userId = SecurityUtil.getCurrentUserId();
        var response = notificationService.getNotifications(userId, filter);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "알림 배지 상태 조회", description = "종 아이콘 배지 표시 여부 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/notifications/unread-status")
    public ResponseEntity<CommonResponse<NotificationUnreadStatusResponse>> getUnreadStatus() {
        var userId = SecurityUtil.getCurrentUserId();
        var response = notificationService.getUnreadStatus(userId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Operation(summary = "알림 읽음 처리", description = "notificationId가 있으면 해당 알림, 없으면 전체 알림 읽음 처리")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "Bad Request",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PatchMapping("/notifications/read")
    public ResponseEntity<CommonResponse<Void>> markNotificationsAsRead(
            @RequestBody(required = false) NotificationReadRequest request
    ) {
        var userId = SecurityUtil.getCurrentUserId();
        var notificationId = request == null ? null : request.notificationId();
        notificationService.markAsRead(userId, notificationId);
        return ResponseEntity.ok(CommonResponse.success());
    }

}
