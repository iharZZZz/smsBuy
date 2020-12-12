package smolka.smsapi.service.receiver.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import smolka.smsapi.dto.*;
import smolka.smsapi.enums.*;
import smolka.smsapi.enums.smshub.SmsHubErrorResponseDictionary;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.exception.UnexpectedResponseException;
import smolka.smsapi.mapper.SmsHubMapper;
import smolka.smsapi.service.receiver.RestReceiver;
import smolka.smsapi.utils.SmsHubRequestCreator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

@Service
@Slf4j
public class SmsHubReceiver implements RestReceiver {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsHubMapper smsHubMapper;

    private static final String SMS_HUB_API_KEY = "41034U7522c7f9198b7c2c479af2cff698870c";
    private static final String RESOURCE_URL = "https://smshub.org/stubs/handler_api.php";

    @Override
    public ReceiverMessage<ReceiverActivationInfoDto> orderActivation(BigDecimal cost, CountryList country, ServiceList service) {
        ResponseEntity<String> response = null;
        try {
            final String actionName = "getNumber";
            HttpEntity<MultiValueMap<String, String>> request = SmsHubRequestCreator.createOrderRequest(SMS_HUB_API_KEY, actionName, country, service);
            response = restTemplate.postForEntity(RESOURCE_URL, request, String.class);
            if (response.getBody() != null && response.getBody().contains("ACCESS_NUMBER")) {
                ReceiverActivationInfoDto activationInfo = smsHubMapper.extractActivationInfoFromResponse(response.getBody());
                return new ReceiverMessage<>(ReceiverActivationInfoDto.class, InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), activationInfo);
            } else {
                ErrorDictionary error = SmsHubErrorResponseDictionary.getError(response.getBody());
                throw new UnexpectedResponseException("Not successful json at orderActivation method", error);
            }
        }
        catch (Exception exc) {
            String errorMessage = response == null || response.getBody() == null ? "NULL" : response.getBody();
            log.error("error processing order with response " +errorMessage);
            throw new InternalErrorException(exc.getMessage());
        }
    }

    @Override
    public ReceiverMessage<ReceiverActivationStatusListDto>  getActivationsStatus() {
        ResponseEntity<String> response = null;
        try {
            final String actionName = "getCurrentActivations";
            HttpEntity<MultiValueMap<String, String>> request = SmsHubRequestCreator.createEmptyRequest(SMS_HUB_API_KEY, actionName);
            response = restTemplate.postForEntity(RESOURCE_URL, request, String.class);
            Map<String, Object> activationStatusMap = smsHubMapper.getMapActivationsStatus(response.getBody());
            if (activationStatusMap.get("status") != null && activationStatusMap.get("status").equals("success")) {
                return new ReceiverMessage<>(ReceiverActivationStatusListDto.class,
                        InternalStatus.OK.getStatusCode(),
                        InternalStatus.OK.getStatusVal(),
                        new ReceiverActivationStatusListDto(smsHubMapper.mapActivationsStatusResponse(activationStatusMap)));
            }
            if (activationStatusMap.get("msg") != null && activationStatusMap.get("msg").equals("no_activations")) {
                return new ReceiverMessage<>(ReceiverActivationStatusListDto.class,
                        InternalStatus.OK.getStatusCode(),
                        InternalStatus.OK.getStatusVal(),
                        new ReceiverActivationStatusListDto(new ArrayList<>()));
            }
            throw new UnexpectedResponseException("Not successful json for activationsStatus", ErrorDictionary.UNKNOWN, response);
        }
        catch (Exception exc) {
            String errorResponse = response == null || response.getBody() == null ? "NULL" :response.getBody();
            log.error("error processing cost map " + errorResponse);
            throw new InternalErrorException(exc.getMessage());
        }
    }

    @Override
    public ReceiverMessage<ReceiverCostMapDto> getCostMap() {
        ResponseEntity<String> response = null;
        try {
            final String actionName = "getNumbersStatusAndCostHubFree";
            HttpEntity<MultiValueMap<String, String>> request = SmsHubRequestCreator.createEmptyRequest(SMS_HUB_API_KEY, actionName);
            response = restTemplate.postForEntity(RESOURCE_URL, request, String.class);
            if (response.getStatusCode().is2xxSuccessful() && !SmsHubErrorResponseDictionary.isError(response.getBody())) {
                return new ReceiverMessage<>(ReceiverCostMapDto.class, InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), smsHubMapper.mapCostMapForSmsHubJson(response.getBody()));
            } else {
                ErrorDictionary error = SmsHubErrorResponseDictionary.getError(response.getBody());
                throw new UnexpectedResponseException("Not successful json at getCostMap method", error);
            }
        } catch (Exception exc) {
            String errorResponse = response == null || response.getBody() == null ? "NULL" :response.getBody();
            log.error("error processing cost map " + errorResponse);
            throw new InternalErrorException(exc.getMessage());
        }
    }
}
