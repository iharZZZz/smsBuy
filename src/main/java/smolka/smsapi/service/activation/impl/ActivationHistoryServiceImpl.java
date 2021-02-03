package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.repository.ActivationHistoryRepository;
import smolka.smsapi.service.activation.ActivationHistoryService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivationHistoryServiceImpl implements ActivationHistoryService {

    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private ActivationHistoryRepository activationHistoryRepository;

    @Override
    @Transactional
    public void saveAllCurrentActivationsToHistoryWithStatus(List<CurrentActivation> currentActivations, ActivationStatus status) {
        List<ActivationHistory> activationHistories = currentActivations.stream().map(curr -> {
            ActivationHistory activationHistory = mainMapper.mapActivationHistoryFromCurrentActivation(curr);
            activationHistory.setStatus(status.getCode());
            activationHistory.setFinishDate(LocalDateTime.now());
            return activationHistory;
        }).collect(Collectors.toList());
        activationHistoryRepository.saveAll(activationHistories);
    }
}
