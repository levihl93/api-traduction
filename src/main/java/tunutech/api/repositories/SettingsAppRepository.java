package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tunutech.api.model.SettingsApp;

public interface SettingsAppRepository extends JpaRepository<SettingsApp, Long> {
}
