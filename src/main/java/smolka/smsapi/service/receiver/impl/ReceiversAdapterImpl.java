package smolka.smsapi.service.receiver.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
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
    public CostMapDto getCommonCostMap() {
        return markUpperService.markUp(mainMapper.mapToInternalCostMap(smsHubReceiver.getCostMap())); // пока что тут будет только получение инфы с СМС-хаба, т.к другие сервисы пока интегрировать рано
    }

    @Override
    public ReceiverActivationInfoDto orderAttempt(Country country, ActivationTarget service, BigDecimal cost) {
        return attemptToSmsHub(country, service, cost);
    }

    @Override
    public CommonReceiversActivationInfoMap getReceiversCurrentActivations() {
        List<ReceiverActivationStatusDto> receiverActivationStatusList = smsHubReceiver.getActivationsStatus();
        return mainMapper.getCommonReceiversActivationInfoMapFromReceiversActivationsList(receiverActivationStatusList);
    }

    private ReceiverActivationInfoDto attemptToSmsHub(Country country, ActivationTarget service, BigDecimal cost) {
        ReceiverCostMapDto costMap = getCostMapFromSmsHub();
        if (!costMap.isExists(country, service, cost)) {
            throw new InternalErrorException("No numbers", ErrorDictionary.NO_NUMBER);
        }
        return smsHubReceiver.orderActivation(country, service);
    }

    private ReceiverCostMapDto getCostMapFromSmsHub() {
        return smsHubReceiver.getCostMap();
    }
}
