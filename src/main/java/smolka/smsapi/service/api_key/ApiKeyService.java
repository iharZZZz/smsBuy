package smolka.smsapi.service.api_key;

import smolka.smsapi.dto.UserKeyDto;
import smolka.smsapi.model.UserKey;

public interface ApiKeyService {
    UserKeyDto create(UserKeyDto userKey);
    UserKeyDto update(UserKeyDto userKey);
    void delete(String userApiKey);
    UserKey findUserKey(String userApiKey);
}
