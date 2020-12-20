package smolka.smsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CostMapDto {
    private final Map<String, Map<BigDecimal, Integer>> costMap = new HashMap<>();

    public boolean isExists(String serviceCode, BigDecimal cost) {
        if (costMap.get(serviceCode) == null) {
            return false;
        }
        Map<BigDecimal, Integer> costs = costMap.get(serviceCode);
        for (BigDecimal costInMap : costs.keySet()) {
            int compareResult = costInMap.compareTo(cost);
            if (compareResult == 0 || compareResult < 0) {
                return true;
            }
        }
        return false;
    }

    public void addCostToMapWithAdd(String serviceCode, BigDecimal cost, Integer count) {
        costMap.computeIfAbsent(serviceCode, k -> new HashMap<>());
        Map<BigDecimal, Integer> costs = costMap.get(serviceCode);
        costs.putIfAbsent(cost, 0);
        costs.put(cost, costs.get(cost) + count);
    }
}
