package smolka.smsapi.service.receiver.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.enums.ErrorDictionary;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;
import smolka.smsapi.service.markupper.MarkUpperService;
import smolka.smsapi.service.receiver.ReceiversAdapter;
import smolka.smsapi.service.receiver.RestReceiver;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ReceiversAdapterImpl implements ReceiversAdapter {

    @Autowired
    private RestReceiver smsHubReceiver;
    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private MarkUpperService markUpperService;

    @Override
    public CostMapDto getCostMap() {
        return markUpperService.markUp(mainMapper.mapToInternalCostMap(smsHubReceiver.getCostMap()));
    }

    @Override
    public ReceiverActivationInfoDto orderAttempt(Country country, ActivationTarget service, BigDecimal cost) {
        CostMapDto costMap = getCostMap();
        if (!costMap.isExists(service.getServiceCode(), cost)) {
            throw new InternalErrorException("No numbers", ErrorDictionary.NO_NUMBER);
        }
        return smsHubReceiver.orderActivation(country, service);
    }

    @Override
    public CommonReceiversActivationInfoMap getReceiversCurrentActivations() {
        CommonReceiversActivationInfoMap receiverActivationInfoMap = new CommonReceiversActivationInfoMap();
        List<ReceiverActivationStatusDto> receiverActivationStatusList = smsHubReceiver.getActivationsStatus();
        for (ReceiverActivationStatusDto receiverActivation : receiverActivationStatusList) {
            receiverActivationInfoMap.addActivationInfo(receiverActivation);
        }
        return receiverActivationInfoMap;
    }
}
