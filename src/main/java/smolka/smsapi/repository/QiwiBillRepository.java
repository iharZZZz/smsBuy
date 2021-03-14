package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.QiwiBill;

public interface QiwiBillRepository extends JpaRepository<QiwiBill, String> {
}
