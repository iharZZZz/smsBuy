package smolka.smsapi.service.api_key;

import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.UserDto;
import smolka.smsapi.model.User;

import java.math.BigDecimal;

public interface UserService {
    void delete(String userApiKey);
    User findUserByUserKey(String userApiKey);
    ServiceMessage<UserDto> getUserInfo(String userApiKey);
    Boolean orderIsPossible(User user, BigDecimal orderCost);
    User subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum);
    User subFromFreezeAndAddToRealBalance(User user, BigDecimal sum);
    User subFromFreeze(User user, BigDecimal sum);
}
