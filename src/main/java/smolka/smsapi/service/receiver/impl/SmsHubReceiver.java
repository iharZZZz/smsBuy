package smolka.smsapi.service.receiver.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.enums.SourceList;
import smolka.smsapi.enums.smshub.SmsHubErrorResponseDictionary;
import smolka.smsapi.exception.ReceiverException;
import smolka.smsapi.mapper.SmsHubMapper;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;
import smolka.smsapi.service.receiver.RestReceiver;
import smolka.smsapi.utils.SmsHubRequestCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service(value = "smsHubReceiver")
public class SmsHubReceiver implements RestReceiver {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsHubMapper smsHubMapper;

    private static final String SMS_HUB_API_KEY = "41034U7522c7f9198b7c2c479af2cff698870c";
    @Value(value = "${sms.api.smshub_url}")
    private String smsHubUrl;

    @Override
    public ReceiverActivationInfoDto orderActivation(Country country, ActivationTarget service) throws ReceiverException {
        ResponseEntity<String> response = null;
        try {
            HttpEntity<MultiValueMap<String, String>> request = SmsHubRequestCreator.createOrderRequest(SMS_HUB_API_KEY, country, service);
            response = restTemplate.postForEntity(smsHubUrl, request, String.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().contains("ACCESS_NUMBER")) {
                return smsHubMapper.extractActivationInfoFromResponse(response.getBody());
            } else {
                throw new Exception("Неизвестный формат ответа");
            }
        } catch (Exception exc) {
            throw new ReceiverException("[SMS-HUB] Ошибка при заказе номера ", response, SourceList.SMSHUB);
        }
    }

    @Override
    public List<ReceiverActivationStatusDto> getActivationsStatus() throws ReceiverException {
        ResponseEntity<String> response = null;
        try {
            final String actionName = "getCurrentActivations";
            HttpEntity<MultiValueMap<String, String>> request = SmsHubRequestCreator.createEmptyRequest(SMS_HUB_API_KEY, actionName);
            response = restTemplate.postForEntity(smsHubUrl, request, String.class);
            Map<String, Object> activationStatusMap = smsHubMapper.getMapActivationsStatus(response.getBody());
            if (activationStatusMap.get("status") != null && activationStatusMap.get("status").equals("success")) {
                return smsHubMapper.mapActivationsStatusResponse(activationStatusMap);
            }
            if (activationStatusMap.get("msg") != null && activationStatusMap.get("msg").equals("no_activations")) {
                return new ArrayList<>();
            }
            throw new Exception("Неизвестный формат ответа");
        } catch (Exception exc) {
            throw new ReceiverException("[SMS-HUB] Ошибка при запросе статуса активаций ", response, SourceList.SMSHUB);
        }
    }

    @Override
    public ReceiverCostMapDto getCostMap(Country country) throws ReceiverException {
        ResponseEntity<String> response = null;
        try {
            HttpEntity<MultiValueMap<String, String>> request = SmsHubRequestCreator.createCostMapRequest(SMS_HUB_API_KEY, country);
            response = restTemplate.postForEntity(smsHubUrl, request, String.class);
            if (response.getStatusCode().is2xxSuccessful() && !SmsHubErrorResponseDictionary.isError(response.getBody())) {
                return smsHubMapper.mapCostMapForSmsHubJson(response.getBody());
            } else {
                throw new Exception("Неизвестный формат ответа");
            }
        } catch (Exception exc) {
            throw new ReceiverException("[SMS-HUB] Ошибка при запросе цен ", response, SourceList.SMSHUB);
        }
    }
}
