package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.enums.*;
import smolka.smsapi.exception.*;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.*;
import smolka.smsapi.repository.ActivationTargetRepository;
import smolka.smsapi.repository.CountryRepository;
import smolka.smsapi.repository.CurrentActivationRepository;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.activation.CurrentActivationService;
import smolka.smsapi.service.api_key.UserService;
import smolka.smsapi.service.receiver.ReceiversAdapter;
import smolka.smsapi.service.sync.BalanceSyncService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private BalanceSyncService balanceSyncService;


    @Override
    @Transactional
    public ServiceMessage<CurrentActivationCreateInfoDto> orderActivation(String apiKey, BigDecimal cost, String serviceCode, String countryCode) throws ReceiverException, UserNotFoundException, UserBalanceIsEmptyException, NoNumbersException {
        User user = userService.findUserByUserKey(apiKey);
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        if (!balanceSyncService.orderIsPossible(user, cost)) {
            throw new UserBalanceIsEmptyException("Не хватает денег");
        }
        ActivationTarget service = activationTargetRepository.findByServiceCode(serviceCode);
        Country country = countryRepository.findByCountryCode(countryCode);
        ReceiverActivationInfoDto receiverActivationInfo = receiversAdapter.orderAttempt(country, service, cost);
        CurrentActivation newActivation = mainMapper.createNewCurrentActivation(receiverActivationInfo, user, country, service, cost, minutesForActivation);
        currentActivationRepository.save(newActivation);
        CurrentActivationCreateInfoDto activationInfo = mainMapper.mapActivationInfoFromActivation(newActivation);
        balanceSyncService.subFromRealBalanceAndAddToFreeze(user, cost);
        return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), activationInfo);
    }

    @Override
    public ServiceMessage<ActivationMessageDto> getCurrentActivationForUser(String apiKey, Long id) throws ActivationNotFoundException, UserNotFoundException {
        User user = userService.findUserByUserKey(apiKey);
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        CurrentActivation activation = currentActivationRepository.findCurrentActivationByIdAndUser(id, user);
        if (activation == null) {
            ActivationHistory activationHistory = activationHistoryService.findActivationHistoryById(id);
            if (activationHistory == null) {
                throw new ActivationNotFoundException("This activation not exist");
            }
            return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), mainMapper.mapActivationMessageFromActivationHistory(activationHistory));
        } else {
            return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), mainMapper.mapActivationMessageFromCurrentActivation(activation));
        }
    }

    @Override
    public ServiceMessage<CurrentActivationsStatusDto> getCurrentActivationsForUser(String apiKey) throws UserNotFoundException {
        User user = userService.findUserByUserKey(apiKey);
        if (user == null) {
            throw new UserNotFoundException("This activation not exist");
        }
        List<CurrentActivation> activations = currentActivationRepository.findAllCurrentActivationsByUser(user);
        return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), mainMapper.mapActivationsStatusFromCurrentActivations(activations));
    }

    @Override
    public ServiceMessage<CostMapDto> getCostsForActivations(String apiKey, String countryCode) throws ReceiverException, UserNotFoundException {
        User user = userService.findUserByUserKey(apiKey);
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует");
        }
        return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), receiversAdapter.getCommonCostMap(countryRepository.findByCountryCode(countryCode)));
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
        balanceSyncService.subFromFreezeAndAddToRealBalance(user, sum);
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
        balanceSyncService.subFromFreeze(user, sum);
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
