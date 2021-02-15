package smolka.smsapi.service.api_key;

import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.UserDto;
import smolka.smsapi.model.User;

public interface UserService {
    void delete(String userApiKey);
    User findUserByUserKey(String userApiKey);
    ServiceMessage<UserDto> getUserInfo(String userApiKey);
}
