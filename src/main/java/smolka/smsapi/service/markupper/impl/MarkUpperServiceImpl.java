package smolka.smsapi.service.markupper.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.CostMapDto;
import smolka.smsapi.model.InternalParameter;
import smolka.smsapi.repository.ParametersRepository;
import smolka.smsapi.service.markupper.MarkUpperService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class MarkUpperServiceImpl implements MarkUpperService {

    @Value(value = "${sms.api.bigdecimal_scaling}")
    private Integer scale;

    private static final String PERCENTAGE_FOR_MARKUPPER_PARAM_NAME = "PERCENTAGE_MARK_UPPER";
    private static final Integer DEFAULT_VALUE = 10;

    @Autowired
    private ParametersRepository parametersRepository;
    private Integer percentageCashedValue;

    @PostConstruct
    public void initialize() {
        InternalParameter percentageForMarkUpper = parametersRepository.findByName(PERCENTAGE_FOR_MARKUPPER_PARAM_NAME);
        percentageCashedValue = percentageForMarkUpper == null ? null : Integer.parseInt(percentageForMarkUpper.getValue());
    }

    @Override
    @Transactional
    public void changePercentage(Integer percentage) {
        InternalParameter percentageForMarkUpper = parametersRepository.findByName(PERCENTAGE_FOR_MARKUPPER_PARAM_NAME);
        if (percentageForMarkUpper == null) {
            percentageForMarkUpper = new InternalParameter(PERCENTAGE_FOR_MARKUPPER_PARAM_NAME, DEFAULT_VALUE.toString());
            percentageCashedValue = DEFAULT_VALUE;
        }
        if (percentage != null) {
            percentageForMarkUpper.setValue(percentage.toString());
            percentageCashedValue = percentage;
        }
        parametersRepository.save(percentageForMarkUpper);
    }

    @Override
    public CostMapDto markUp(CostMapDto costMapDto) {
        Integer percentage = percentageCashedValue == null ? DEFAULT_VALUE : percentageCashedValue;
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
