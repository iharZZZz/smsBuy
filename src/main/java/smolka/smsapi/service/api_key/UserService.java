package smolka.smsapi.service.api_key;

import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.UserDto;
import smolka.smsapi.dto.input.UserKeyDto;
import smolka.smsapi.model.User;

import java.math.BigDecimal;

public interface UserService {
    UserKeyDto create(UserKeyDto userKey);
    UserKeyDto update(UserKeyDto userKey);
    void delete(String userApiKey);
    User findUserKey(String userApiKey);
    ServiceMessage<UserDto> getUserInfo(String userApiKey);
    User subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum);
    User subFromFreezeAndAddToRealBalance(User user, BigDecimal sum);
    User subFromFreeze(User user, BigDecimal sum);
}
