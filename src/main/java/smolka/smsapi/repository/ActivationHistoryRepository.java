package smolka.smsapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.User;

public interface ActivationHistoryRepository extends JpaRepository<ActivationHistory, Long> {
    Page<ActivationHistory> findAllByUser(Pageable pageable, User user);
    Long countByUser(User user);
}
