package smolka.smsapi.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.ActivationInfoDto;
import smolka.smsapi.dto.ActivationStatusDto;
import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.model.*;
import smolka.smsapi.repository.ActivationTargetRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MainMapper {

    @Value(value = "${sms.api.bigdecimal_scaling}")
    private Integer scale;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ActivationTargetRepository activationTargetRepository;

    public <E, D> D mapping(E object, Class<D> clazz) {
        if (object == null) {
            return null;
        }
        return modelMapper.map(object, clazz);
    }

    public <E, D> List<D> toListMapping(Collection<E> objects, Class<D> cls) {
        if (objects == null) {
            return new ArrayList<>();
        }
        return objects.stream().map(i -> mapping(i, cls)).collect(Collectors.toList());
    }

    public CostMapDto mapToInternalCostMap(ReceiverCostMapDto costMap) {
        CostMapDto internalCostMap = new CostMapDto();
        if (costMap == null || costMap.getCostMap().isEmpty()) {
            return internalCostMap;
        }
        for (ActivationTarget service : costMap.getCostMap().keySet()) {
            Map<BigDecimal, Integer> costs = costMap.getCostMap().get(service);
            for (BigDecimal cost : costs.keySet()) {
                BigDecimal newCost = new BigDecimal(cost.toString());
                newCost = newCost.setScale(scale, RoundingMode.CEILING);
                String internalServiceCode = service.getServiceCode();
                internalCostMap.addCostToMapWithAdd(internalServiceCode, newCost, costs.get(cost));
            }
        }
        return internalCostMap;
    }

    public ActivationStatusDto mapActivationStatusFromActivation(CurrentActivation activation) {
        ActivationStatusDto activationStatus = mapping(activation, ActivationStatusDto.class);
        activationStatus.setCountryCode(activation.getCountry().getCountryCode());
        activationStatus.setServiceCode(activation.getService().getServiceCode());
        return activationStatus;
    }

    public ActivationInfoDto mapActivationInfoFromActivation(CurrentActivation activation) {
        ActivationInfoDto activationInfo = mapping(activation, ActivationInfoDto.class);
        activationInfo.setCountryCode(activation.getCountry().getCountryCode());
        activationInfo.setServiceCode(activation.getService().getServiceCode());
        return activationInfo;
    }

    public ActivationHistory mapActivationHistoryFromCurrentActivation(CurrentActivation currActivation) {
        return ActivationHistory.builder()
                .id(currActivation.getId())
                .user(currActivation.getUser())
                .number(currActivation.getNumber())
                .message(currActivation.getMessage())
                .country(currActivation.getCountry())
                .service(currActivation.getService())
                .source(currActivation.getSource())
                .createDate(currActivation.getCreateDate())
                .sourceId(currActivation.getSourceId())
                .status(currActivation.getStatus())
                .cost(currActivation.getCost())
                .build();
    }

    public CurrentActivation createNewCurrentActivation(ReceiverActivationInfoDto receiverActivationInfo,
                                                        User user,
                                                        Country country,
                                                        ActivationTarget service,
                                                        BigDecimal cost,
                                                        Integer minutesForActivation) {
        LocalDateTime createDate = LocalDateTime.now();
        return CurrentActivation.builder()
                .user(user)
                .number(receiverActivationInfo.getNumber())
                .message(null)
                .country(country)
                .service(service)
                .source(receiverActivationInfo.getSource())
                .createDate(LocalDateTime.now())
                .plannedFinishDate(createDate.plusMinutes(minutesForActivation))
                .sourceId(receiverActivationInfo.getId())
                .status(ActivationStatus.ACTIVE.getCode())
                .cost(cost)
                .build();
    }

    public CommonReceiversActivationInfoMap getCommonReceiversActivationInfoMapFromReceiversActivationsList(List<ReceiverActivationStatusDto> receiverActivationStatusList) {
        CommonReceiversActivationInfoMap receiverActivationInfoMap = new CommonReceiversActivationInfoMap();
        for (ReceiverActivationStatusDto receiverActivation : receiverActivationStatusList) {
            receiverActivationInfoMap.addActivationInfo(receiverActivation);
        }
        return receiverActivationInfoMap;
    }
}
