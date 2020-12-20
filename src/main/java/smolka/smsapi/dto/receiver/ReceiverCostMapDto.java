package smolka.smsapi.dto.receiver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import smolka.smsapi.enums.SourceList;
import smolka.smsapi.model.ActivationTarget;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class ReceiverCostMapDto {

    private final Map<ActivationTarget, Map<BigDecimal, Integer>> costMap = new HashMap<>();
    private SourceList source;

    public void addCostToMap(ActivationTarget service, BigDecimal cost, Integer count) {
        costMap.computeIfAbsent(service, k -> new HashMap<>());
        costMap.get(service).put(cost, count);
    }
}
