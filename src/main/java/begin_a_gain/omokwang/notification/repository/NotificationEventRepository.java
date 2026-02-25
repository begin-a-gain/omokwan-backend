package begin_a_gain.omokwang.notification.repository;

import begin_a_gain.omokwang.notification.domain.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationEventRepository extends JpaRepository<NotificationEvent, Long> {
}
