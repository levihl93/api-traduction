package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
