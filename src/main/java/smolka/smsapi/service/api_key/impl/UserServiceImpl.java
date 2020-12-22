package smolka.smsapi.service.api_key.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.UserKeyDto;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.UserRepository;
import smolka.smsapi.service.api_key.UserService;

import java.math.BigDecimal;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MainMapper mainMapper;

    @Override
    @Transactional
    public UserKeyDto create(UserKeyDto userKey) {
        try {
            User userEntity = mainMapper.mapping(userKey, User.class);
            return mainMapper.mapping(userRepository.save(userEntity), UserKeyDto.class);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserKeyDto update(UserKeyDto userKey) {
        try {
            User existingEntity = userRepository.findUserByUserId(userKey.getUserId());
            if (existingEntity == null) {
                throw new InternalErrorException("This entity doesnt exists!");
            }
            return mainMapper.mapping(userRepository.save(existingEntity), UserKeyDto.class);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

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
    @Transactional(readOnly = true)
    public User findUserKey(String userApiKey) {
        try {
            return userRepository.findUserByKey(userApiKey);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void addBalanceForUser(User user, BigDecimal money) {
        user.setBalance(user.getBalance().add(money));
        userRepository.save(user);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void subBalanceForUser(User user, BigDecimal money) {
        user.setBalance(user.getBalance().subtract(money));
        userRepository.save(user);
    }
}
