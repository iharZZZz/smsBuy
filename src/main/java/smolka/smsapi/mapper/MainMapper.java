package smolka.smsapi.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.dto.receiver.ReceiverCostMapDto;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.repository.ActivationTargetRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MainMapper {

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ActivationTargetRepository activationTargetRepository;

    private static final Integer SCALE = 2;

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
                newCost = newCost.setScale(SCALE, RoundingMode.CEILING);
                String internalServiceCode = service.getServiceCode();
                internalCostMap.addCostToMapWithAdd(internalServiceCode, newCost, costs.get(cost));
            }
        }
        return internalCostMap;
    }
}
