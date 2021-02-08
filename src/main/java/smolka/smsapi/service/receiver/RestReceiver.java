package smolka.smsapi.service.receiver;

import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.model.Country;
import smolka.smsapi.model.ActivationTarget;

import java.util.List;

public interface RestReceiver {
    ReceiverActivationInfoDto orderActivation(Country country, ActivationTarget service);
    List<ReceiverActivationStatusDto> getActivationsStatus();
    ReceiverCostMapDto getCostMap(Country country);
}
