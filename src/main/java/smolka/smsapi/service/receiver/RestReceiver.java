package smolka.smsapi.service.receiver;

import smolka.smsapi.dto.*;
import smolka.smsapi.enums.CountryList;
import smolka.smsapi.enums.ServiceList;

import java.math.BigDecimal;

public interface RestReceiver {
    ReceiverMessage<ReceiverActivationInfoDto> orderActivation(BigDecimal cost, CountryList country, ServiceList service);
    ReceiverMessage<ReceiverActivationStatusListDto> getActivationsStatus();
    ReceiverMessage<ReceiverCostMapDto> getCostMap();
}
