package smolka.smsapi.service.activation;

import smolka.smsapi.dto.*;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CurrentActivationService {
    ServiceMessage<CurrentActivationCreateInfoDto> orderActivation(String apiKey, BigDecimal cost, String serviceCode, String countryCode);
    ServiceMessage<ActivationMessageDto> getCurrentActivationForUser(String apiKey, Long id);
    ServiceMessage<CurrentActivationsStatusDto> getCurrentActivationsForUser(String apiKey);
    CostMapDto getCostsForActivations(String apiKey);
    void setMessageForCurrentActivation(CurrentActivation activation, String message);
    List<CurrentActivation> findAllCurrentActivationsWithoutReceivedMessage();
    void closeCurrentActivationsForUser(User user, List<CurrentActivation> activationsForClose);
    void succeedCurrentActivationsForUser(User user, List<CurrentActivation> activationsForSucceed);
    Map<User, List<CurrentActivation>> findAllCurrentExpiredActivationsForUsers();
}