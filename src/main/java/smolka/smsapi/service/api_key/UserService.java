package smolka.smsapi.service.api_key;

import smolka.smsapi.dto.UserDto;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.model.User;

public interface UserService {
    void delete(String userApiKey);

    User findUserByUserKey(String userApiKey);

    UserDto getUserInfo(String userApiKey) throws UserNotFoundException;
//    void testSaveUser(String newApiKey);
}
