package smolka.smsapi.service.api_key.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.UserDto;
import smolka.smsapi.enums.SmsConstants;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.UserRepository;
import smolka.smsapi.service.api_key.UserService;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MainMapper mainMapper;

    @Override
    @Transactional
    public void delete(String userApiKey) {
        try {
            userRepository.deleteByKey(userApiKey);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    public User findUserByUserKey(String userApiKey) {
        try {
            return userRepository.findUserByKey(userApiKey);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    public ServiceMessage<UserDto> getUserInfo(String userApiKey) {
        try {
            User user = userRepository.findUserByKey(userApiKey);
            if (user == null) {
                throw new InternalErrorException("Данного юзера не существует");
            }
            return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), mainMapper.mapping(user, UserDto.class));
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }
}
