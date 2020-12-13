package smolka.smsapi.service.api_key.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.UserKeyDto;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.UserKey;
import smolka.smsapi.repository.UserKeyRepository;
import smolka.smsapi.service.api_key.ApiKeyService;

@Service
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {

    @Autowired
    private UserKeyRepository userKeyRepository;

    @Autowired
    private MainMapper mainMapper;

    @Override
    @Transactional
    public UserKeyDto create(UserKeyDto userKey) {
        try {
            UserKey userKeyEntity = mainMapper.mapping(userKey, UserKey.class);
            return mainMapper.mapping(userKeyRepository.save(userKeyEntity), UserKeyDto.class);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserKeyDto update(UserKeyDto userKey) {
        try {
            UserKey existingEntity = userKeyRepository.findUserKeyByUserId(userKey.getUserId());
            if (existingEntity == null) {
                throw new InternalErrorException("This entity doesnt exists!");
            }
            return mainMapper.mapping(userKeyRepository.save(existingEntity), UserKeyDto.class);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void delete(String userApiKey) {
        try {
            userKeyRepository.deleteByKey(userApiKey);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserKey findUserKey(String userApiKey) {
        try {
            return userKeyRepository.findUserKeyByKey(userApiKey);
        }
        catch (Exception e) {
            throw new InternalErrorException(e.getMessage());
        }
    }
}
