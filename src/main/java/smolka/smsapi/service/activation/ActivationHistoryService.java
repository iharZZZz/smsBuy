package smolka.smsapi.service.activation;

import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.model.CurrentActivation;

import java.util.List;

public interface ActivationHistoryService {
    void saveAllCurrentActivationsToHistoryWithStatus(List<CurrentActivation> currentActivations, ActivationStatus status);
}
