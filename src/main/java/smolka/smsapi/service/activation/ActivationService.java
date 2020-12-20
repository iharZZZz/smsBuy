package smolka.smsapi.service.activation;

import smolka.smsapi.dto.*;
import smolka.smsapi.model.Activation;

import java.math.BigDecimal;
import java.util.List;

public interface ActivationService {
    ServiceMessage<ActivationInfoDto> orderActivation(String apiKey, BigDecimal cost, String serviceCode, String countryCode);
    ServiceMessage<ActivationStatusDto> getActivationForUser(String apiKey, Long id);
    ServiceMessage<ActivationsStatusDto> getActivationsForUser(String apiKey);
    void setMessageForActivation(Activation activation, String message);
    Activation closeActivation(Activation activation);
    Activation succeedActivation(Activation activation);
    List<Activation> findAllInternalCurrentActivations();
    List<Activation> findAllExpiredActivations();
    ReceiverActivationInfoMap getReceiversCurrentActivations();
}
