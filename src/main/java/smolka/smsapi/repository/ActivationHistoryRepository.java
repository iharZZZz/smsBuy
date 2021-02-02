package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.ActivationHistory;

public interface ActivationHistoryRepository extends JpaRepository<ActivationHistory, Long> {
}
