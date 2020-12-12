package smolka.smsapi.service.api_key.impl;

import org.springframework.stereotype.Service;
import smolka.smsapi.service.api_key.ApiKeyService;

@Service
public class ApiKeyConstantServiceImpl implements ApiKeyService {

    private static final String EXISTING_API_KEY = "1";

    @Override
    public boolean apiKeyIsExist(String apiKey) {
        return EXISTING_API_KEY.equals(apiKey);
    }
}
