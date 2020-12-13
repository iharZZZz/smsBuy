package smolka.smsapi.service.activation;

import smolka.smsapi.dto.ActivationInfoDto;
import smolka.smsapi.dto.ActivationStatusDto;
import smolka.smsapi.dto.ActivationsStatusDto;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.enums.CountryList;
import smolka.smsapi.enums.ServiceList;
import smolka.smsapi.enums.SourceList;
import smolka.smsapi.model.UserKey;

import java.math.BigDecimal;

public interface ActivationService {
    ServiceMessage<ActivationInfoDto> orderActivation(String apiKey, BigDecimal cost, ServiceList service, CountryList country);
    ServiceMessage<ActivationStatusDto> getActivation(String apiKey, Long id);
    ServiceMessage<ActivationsStatusDto> getActivations(String apiKey);
    void succeedActivation(Long id, String message);
    void closeActivation(Long id);
}
