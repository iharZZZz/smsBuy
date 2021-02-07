package smolka.smsapi.service.activation;

import smolka.smsapi.dto.ActivationHistoryDto;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.input.GetActivationHistoryRequest;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.CurrentActivation;

import java.util.List;

public interface ActivationHistoryService {
    ServiceMessage<ActivationHistoryDto> getActivationHistorySortedByFinishDateDesc(GetActivationHistoryRequest getActivationHistoryRequest);
    void saveAllCurrentActivationsToHistoryWithStatus(List<CurrentActivation> currentActivations, ActivationStatus status);
    ActivationHistory findActivationHistoryById(Long id);
}
