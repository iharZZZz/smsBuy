package smolka.smsapi.service.activation;

import smolka.smsapi.dto.*;
import smolka.smsapi.model.Activation;
import smolka.smsapi.model.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ActivationService {
    ServiceMessage<ActivationInfoDto> orderActivation(String apiKey, BigDecimal cost, String serviceCode, String countryCode);
    ServiceMessage<ActivationStatusDto> getActivationForUser(String apiKey, Long id);
    ServiceMessage<ActivationsStatusDto> getCurrentActivationsForUser(String apiKey);
    void setMessageForActivation(Activation activation, String message);
    List<Activation> findAllInternalActiveActivations();
    void closeActivationsForUser(User user, List<Activation> activationsForClose);
    void succeedActivationsForUser(User user, List<Activation> activationsForSucceed);
    Map<User, List<Activation>> findAllExpiredActivationsForUsers();
    CommonReceiversActivationInfoMap getReceiversCurrentActivations();
}
