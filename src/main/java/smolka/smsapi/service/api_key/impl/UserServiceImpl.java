package smolka.smsapi.service.api_key.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.UserDto;
import smolka.smsapi.dto.input.UserKeyDto;
import smolka.smsapi.enums.InternalStatus;
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
            userEntity.setFreezeBalance(BigDecimal.ZERO);
            userEntity.setBalance(BigDecimal.ZERO);
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
    public ServiceMessage<UserDto> getUserInfo(String userApiKey) {
        try {
            User user = userRepository.findUserByKey(userApiKey);
            if (user == null) {
                throw new InternalErrorException("Данного юзера не существует");
            }
            return new ServiceMessage<>(InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), mainMapper.mapping(user, UserDto.class));
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum) {
        BigDecimal subBalance = user.getBalance().subtract(sum);
        if (subBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
        user.setBalance(subBalance);
        user.setFreezeBalance(user.getFreezeBalance().add(sum));
        return userRepository.save(user);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User subFromFreezeAndAddToRealBalance(User user, BigDecimal sum) {
        BigDecimal subFreezeBalance = user.getFreezeBalance().subtract(sum);
        if (subFreezeBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Замороженный баланс не может быть отрицательным");
        }
        user.setFreezeBalance(user.getBalance());
        user.setBalance(user.getBalance().add(sum));
        return userRepository.save(user);
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public User subFromFreeze(User user, BigDecimal sum) {
        BigDecimal subFreezeBalance = user.getFreezeBalance().subtract(sum);
        if (subFreezeBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Замороженный баланс не может быть отрицательным");
        }
        user.setFreezeBalance(subFreezeBalance);
        return userRepository.save(user);
    }
}
