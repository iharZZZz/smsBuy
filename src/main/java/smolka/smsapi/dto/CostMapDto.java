package smolka.smsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import smolka.smsapi.enums.ServiceList;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostMapDto {
    private final Map<ServiceList, Map<BigDecimal, Integer>> costMap = new HashMap<>();

    public boolean isExists(ServiceList service, BigDecimal cost) {
        if (costMap.get(service) == null) {
            return false;
        }
        Map<BigDecimal, Integer> costs = costMap.get(service);
        for (BigDecimal costInMap : costs.keySet()) {
            int compareResult = costInMap.compareTo(cost);
            if (compareResult == 0 || compareResult < 0) {
                return true;
            }
        }
        return false;
    }

    public void addCostToMapWithAdd(ServiceList service, BigDecimal cost, Integer count) {
        costMap.computeIfAbsent(service, k -> new HashMap<>());
        Map<BigDecimal, Integer> costs = costMap.get(service);
        costs.putIfAbsent(cost, 0);
        costs.put(cost, costs.get(cost) + count);
    }
}
