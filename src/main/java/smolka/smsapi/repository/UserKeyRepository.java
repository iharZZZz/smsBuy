package smolka.smsapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import smolka.smsapi.model.UserKey;

public interface UserKeyRepository extends JpaRepository<UserKey, Long> {
    UserKey findUserKeyByKey(String key);
    UserKey findUserKeyByUserId(Long userId);
}
