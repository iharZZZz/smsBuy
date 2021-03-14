package smolka.smsapi.service.markupper.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.service.markupper.MarkUpperService;
import smolka.smsapi.service.parameters_service.ParametersService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class MarkUpperServiceImpl implements MarkUpperService {

    @Value(value = "${sms.api.bigdecimal_scaling}")
    private Integer scale;

    @Autowired
    private ParametersService parametersService;

    private Integer percentageCashedValue;

    @PostConstruct
    public void initialize() {
        percentageCashedValue = parametersService.getPercentageForMarkUpper();
    }

    @Override
    @Transactional
    public void changePercentage(Integer percentage) {
        percentageCashedValue = percentage;
        parametersService.savePercentageForMarkUpper(percentageCashedValue);
    }

    @Override
    public CostMapDto markUp(CostMapDto costMapDto) {
        Integer percentage = percentageCashedValue;
        if (percentage == 0) {
            return costMapDto;
        }
        for (String country : costMapDto.getCostMap().keySet()) {
            for (String srv : costMapDto.getCostMap().get(country).keySet()) {
                Map<BigDecimal, Integer> costs = costMapDto.getCostMap().get(country).get(srv);
                for (BigDecimal val : costs.keySet()) {
                    Integer tmpCount = costs.get(val);
                    costs.put(val.add(percentage(val, percentage)).setScale(scale, RoundingMode.CEILING), tmpCount);
                    costs.remove(val);
                }
            }
        }
        return costMapDto;
    }

    private BigDecimal percentage(BigDecimal base, Integer pct) {
        return base.multiply(new BigDecimal(pct)).divide(new BigDecimal(100), RoundingMode.CEILING).setScale(scale, RoundingMode.CEILING);
    }
}
