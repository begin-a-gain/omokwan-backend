package begin_a_gain.omokwang.notification.controller;

import begin_a_gain.omokwang.auth.utils.SecurityUtil;
import begin_a_gain.omokwang.common.response.CommonResponse;
import begin_a_gain.omokwang.notification.dto.NotificationListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import begin_a_gain.omokwang.notification.application.NotificationService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = "Notification", description = "Notification API")
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "전체/안읽음 알림 목록 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
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

}
