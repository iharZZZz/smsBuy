package smolka.smsapi.service.api_key;

import smolka.smsapi.dto.UserKeyDto;
import smolka.smsapi.model.User;

import java.math.BigDecimal;

public interface UserService {
    UserKeyDto create(UserKeyDto userKey);
    UserKeyDto update(UserKeyDto userKey);
    void delete(String userApiKey);
    User findUserKey(String userApiKey);
    void addBalanceForUser(User user, BigDecimal money);
    void subBalanceForUser(User user, BigDecimal money);
}
