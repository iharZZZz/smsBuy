package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.enums.*;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.*;
import smolka.smsapi.repository.ActivationHistoryRepository;
import smolka.smsapi.repository.ActivationTargetRepository;
import smolka.smsapi.repository.CountryRepository;
import smolka.smsapi.repository.CurrentActivationRepository;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.activation.CurrentActivationService;
import smolka.smsapi.service.api_key.UserService;
import smolka.smsapi.service.receiver.ReceiversAdapter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurrentActivationServiceImpl implements CurrentActivationService {

    @Value("${sms.api.minutes_for_activation:20}")
    private Integer minutesForActivation;
    @Autowired
    private CurrentActivationRepository currentActivationRepository;
    @Autowired
    private ActivationHistoryService activationHistoryService;
    @Autowired
    private ActivationTargetRepository activationTargetRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private ReceiversAdapter receiversAdapter;
    @Autowired
    private UserService userService;


    @Override
    @Transactional
    public ServiceMessage<ActivationInfoDto> orderActivation(String apiKey, BigDecimal cost, String serviceCode, String countryCode) {
        User user = userService.findUserKey(apiKey);
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        if (user.getBalance().compareTo(cost) < 0) { // TODO: слабое место
            throw new InternalErrorException("User balance is empty", ErrorDictionary.NO_BALANCE);
        }
        ActivationTarget service = activationTargetRepository.findByServiceCode(serviceCode);
        Country country = countryRepository.findByCountryCode(countryCode);
        ReceiverActivationInfoDto receiverActivationInfo = receiversAdapter.orderAttempt(country, service, cost);
        CurrentActivation newActivation = mainMapper.createNewCurrentActivation(receiverActivationInfo, user, country, service, cost, minutesForActivation);
        currentActivationRepository.save(newActivation);
        ActivationInfoDto activationInfo = mainMapper.mapActivationInfoFromActivation(newActivation);
        userService.subFromRealBalanceAndAddToFreeze(user, cost);
        return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), activationInfo);
    }

    @Override
    public ServiceMessage<ActivationStatusDto> getCurrentActivationForUser(String apiKey, Long id) {
        User user = userService.findUserKey(apiKey);
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        CurrentActivation activation = currentActivationRepository.findCurrentActivationByIdAndUser(id, user);
        if (activation == null) {
            throw new InternalErrorException("This activation not exist", ErrorDictionary.NO_ACTIVATION);
        }
        ActivationStatusDto activationStatus = mainMapper.mapActivationStatusFromActivation(activation);
        return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), activationStatus);
    }

    @Override
    public ServiceMessage<ActivationsStatusDto> getCurrentActivationsForUser(String apiKey) {
        User user = userService.findUserKey(apiKey);
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        List<CurrentActivation> activations = currentActivationRepository.findAllCurrentActivationsByUser(user);
        List<ActivationStatusDto> activationStatusList = activations.stream().map(a -> mainMapper.mapActivationStatusFromActivation(a)).collect(Collectors.toList()); // TODO когда вынесу все остальное в маппер поправить и здесь
        ActivationsStatusDto activationsStatusDto = new ActivationsStatusDto(activationStatusList);
        return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), activationsStatusDto);
    }

    @Override
    public CostMapDto getCostsForActivations(String apiKey) {
        User user = userService.findUserKey(apiKey);
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        return receiversAdapter.getCostMap();
    }

    @Override
    @Transactional
    public void setMessageForCurrentActivation(CurrentActivation activation, String message) {
        activation.setMessage(message);
        activation.setStatus(ActivationStatus.SMS_RECEIVED.getCode());
        currentActivationRepository.save(activation);
    }

    @Override
    public List<CurrentActivation> findAllCurrentActivationsWithoutReceivedMessage() {
        return currentActivationRepository.findAllCurrentActivationsByStatus(ActivationStatus.ACTIVE.getCode());
    }

    @Override
    @Transactional
    public void closeCurrentActivationsForUser(User user, List<CurrentActivation> activationsForClose) {
        if (activationsForClose.isEmpty()) {
            return;
        }
        activationHistoryService.saveAllCurrentActivationsToHistoryWithStatus(activationsForClose, ActivationStatus.CLOSED);
        currentActivationRepository.deleteAll(activationsForClose);
        BigDecimal sum = activationsForClose.stream()
                .map(CurrentActivation::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userService.subFromFreezeAndAddToRealBalance(user, sum);
    }

    @Override
    @Transactional
    public void succeedCurrentActivationsForUser(User user, List<CurrentActivation> activationsForSucceed) {
        if (activationsForSucceed.isEmpty()) {
            return;
        }
        activationHistoryService.saveAllCurrentActivationsToHistoryWithStatus(activationsForSucceed, ActivationStatus.SUCCEED);
        currentActivationRepository.deleteAll(activationsForSucceed);
        BigDecimal sum = activationsForSucceed.stream()
                .map(CurrentActivation::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        userService.subFromFreeze(user, sum);
    }

    @Override
    public Map<User, List<CurrentActivation>> findAllCurrentExpiredActivationsForUsers() {
        List<CurrentActivation> expiredActivations = currentActivationRepository.findAllCurrentActivationsByPlannedFinishDateLessThanEqual(LocalDateTime.now());
        Map<User, List<CurrentActivation>> resultMap = new HashMap<>();
        for (CurrentActivation activation : expiredActivations) {
            User userForActivation = activation.getUser();
            resultMap.computeIfAbsent(userForActivation, k -> new ArrayList<>());
            resultMap.get(userForActivation).add(activation);
        }
        return resultMap;
    }
}
