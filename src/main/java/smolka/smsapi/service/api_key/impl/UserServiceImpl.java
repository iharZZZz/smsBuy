package smolka.smsapi.service.api_key.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.UserDto;
import smolka.smsapi.enums.SmsConstants;
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

    @Override
    public Boolean orderIsPossible(User user, BigDecimal orderCost) {
        user = userRepository.findUserByUserId(user.getUserId());
        if (user == null) {
            throw new InternalErrorException("Данного юзера не существует");
        }
        return user.getBalance().compareTo(orderCost) >= 0;
    }

    @Override
    @Transactional
    public User subFromRealBalanceAndAddToFreeze(User user, BigDecimal sum) {
        user = userRepository.findUserByUserId(user.getUserId());
        if (user == null) {
            throw new InternalErrorException("Данного юзера не существует");
        }
        BigDecimal subBalance = user.getBalance().subtract(sum);
        if (subBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Баланс не может быть отрицательным");
        }
        user.setBalance(subBalance);
        user.setFreezeBalance(user.getFreezeBalance().add(sum));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User subFromFreezeAndAddToRealBalance(User user, BigDecimal sum) {
        user = userRepository.findUserByUserId(user.getUserId());
        if (user == null) {
            throw new InternalErrorException("Данного юзера не существует");
        }
        BigDecimal subFreezeBalance = user.getFreezeBalance().subtract(sum);
        if (subFreezeBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Замороженный баланс не может быть отрицательным");
        }
        user.setFreezeBalance(user.getBalance());
        user.setBalance(user.getBalance().add(sum));
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User subFromFreeze(User user, BigDecimal sum) {
        user = userRepository.findUserByUserId(user.getUserId());
        if (user == null) {
            throw new InternalErrorException("Данного юзера не существует");
        }
        BigDecimal subFreezeBalance = user.getFreezeBalance().subtract(sum);
        if (subFreezeBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Замороженный баланс не может быть отрицательным");
        }
        user.setFreezeBalance(subFreezeBalance);
        return userRepository.save(user);
    }
}
