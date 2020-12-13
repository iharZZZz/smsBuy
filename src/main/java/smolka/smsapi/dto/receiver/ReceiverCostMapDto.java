package smolka.smsapi.dto.receiver;

import lombok.Getter;
import lombok.NoArgsConstructor;
import smolka.smsapi.enums.ServiceList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
public class ReceiverCostMapDto {

    private final Map<ServiceList, Map<BigDecimal, Integer>> costMap = new HashMap<>();

    public void addCostToMap(ServiceList service, BigDecimal cost, Integer count) {
        costMap.computeIfAbsent(service, k -> new HashMap<>());
        costMap.get(service).put(cost, count);
    }
}
