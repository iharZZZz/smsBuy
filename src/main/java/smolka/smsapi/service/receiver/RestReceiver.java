package smolka.smsapi.service.receiver;

import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusListDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.enums.CountryList;
import smolka.smsapi.enums.ServiceList;

import java.math.BigDecimal;

public interface RestReceiver {
    ReceiverActivationInfoDto orderActivation(CountryList country, ServiceList service);
    ReceiverActivationStatusListDto getActivationsStatus();
    ReceiverCostMapDto getCostMap();
}
