package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.enums.*;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.Activation;
import smolka.smsapi.model.ActivationTarget;
import smolka.smsapi.model.Country;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.ActivationRepository;
import smolka.smsapi.repository.ActivationTargetRepository;
import smolka.smsapi.repository.CountryRepository;
import smolka.smsapi.service.activation.ActivationService;
import smolka.smsapi.service.api_key.UserService;
import smolka.smsapi.service.receiver.RestReceiver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivationServiceImpl implements ActivationService {

    @Value("${sms.api.minutes_for_activation:20}")
    private Integer minutesForActivation;
    @Autowired
    private ActivationRepository activationRepository;
    @Autowired
    private ActivationTargetRepository activationTargetRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RestReceiver smsHubReceiver;

    @Override
    @Transactional
    public ServiceMessage<ActivationInfoDto> orderActivation(String apiKey, BigDecimal cost, String serviceCode, String countryCode) {
        User user = userService.findUserKey(apiKey);
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        if (user.getBalance().compareTo(cost) < 0) {
            throw new InternalErrorException("User balance is empty", ErrorDictionary.NO_BALANCE);
        }
        ActivationTarget service = activationTargetRepository.findByServiceCode(serviceCode);
        Country country = countryRepository.findByCountryCode(countryCode);
        CostMapDto costMap = mainMapper.mapToInternalCostMap(smsHubReceiver.getCostMap());
        if (!costMap.isExists(service.getServiceCode(), cost)) {
            throw new InternalErrorException("No numbers", ErrorDictionary.NO_NUMBER);
        }
        ReceiverActivationInfoDto receiverActivationInfo = smsHubReceiver.orderActivation(country, service);
        ActivationInfoDto activationInfo = this.createActivation(receiverActivationInfo, user, country, service, SourceList.SMSHUB, cost);
        return new ServiceMessage<>(InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), activationInfo);
    }

    @Override
    public ServiceMessage<ActivationStatusDto> getActivationForUser(String apiKey, Long id) {
        User user = userService.findUserKey(apiKey);
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        Activation activation = activationRepository.findActivationByIdAndUser(id, user);
        if (activation == null) {
            throw new InternalErrorException("This activation not exist", ErrorDictionary.NO_ACTIVATION);
        }
        ActivationStatusDto activationStatus = mainMapper.mapActivationStatusFromActivation(activation);
        return new ServiceMessage<>(InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), activationStatus);
    }

    @Override
    public ServiceMessage<ActivationsStatusDto> getCurrentActivationsForUser(String apiKey) {
        User user = userService.findUserKey(apiKey);
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        List<Activation> activations = activationRepository.findAllCurrentActivationsByUser(user);
        List<ActivationStatusDto> activationStatusList = activations.stream().map(a -> mainMapper.mapActivationStatusFromActivation(a)).collect(Collectors.toList()); // TODO когда вынесу все остальное в маппер поправить и здесь
        ActivationsStatusDto activationsStatusDto = new ActivationsStatusDto(activationStatusList);
        return new ServiceMessage<>(InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), activationsStatusDto);
    }

    @Override
    @Transactional
    public void setMessageForActivation(Activation activation, String message) {
        activation.setMessage(message);
        activation.setStatus(ActivationStatus.SMS_RECEIVED.getCode());
        activationRepository.save(activation);
    }

    @Override
    @Transactional
    public Activation closeActivation(Activation activation) {
        activation.setFinishDate(LocalDateTime.now());
        activation.setStatus(ActivationStatus.CLOSED.getCode());
        activation = activationRepository.save(activation);
        userService.addBalanceForUser(activation.getUser(), activation.getCost());
        return activation;
    }

    @Override
    @Transactional
    public Activation succeedActivation(Activation activation) {
        activation.setStatus(ActivationStatus.SUCCEED.getCode());
        activation.setFinishDate(LocalDateTime.now());
        activation = activationRepository.save(activation);
        userService.subBalanceForUser(activation.getUser(), activation.getCost());
        return activation;
    }

    @Override
    public List<Activation> findAllInternalActiveActivations() {
        return activationRepository.findAllActivationsByStatus(ActivationStatus.ACTIVE.getCode());
    }

    @Override
    public List<Activation> findAllExpiredActivations() {
        return activationRepository.findAllActivationsByPlannedFinishDateLessThanEqual(LocalDateTime.now());
    }

    @Override
    public CommonReceiversActivationInfoMap getReceiversCurrentActivations() {
        CommonReceiversActivationInfoMap receiverActivationInfoMap = new CommonReceiversActivationInfoMap();
        List<ReceiverActivationStatusDto> receiverActivationStatusList = smsHubReceiver.getActivationsStatus();
        for (ReceiverActivationStatusDto receiverActivation : receiverActivationStatusList) {
            receiverActivationInfoMap.addActivationInfo(receiverActivation);
        }
        return receiverActivationInfoMap;
    }



    @Transactional
    private ActivationInfoDto createActivation(ReceiverActivationInfoDto receiverActivationInfo,
                                               User user,
                                               Country country,
                                               ActivationTarget service,
                                               SourceList source,
                                               BigDecimal cost) {
        LocalDateTime createDate = LocalDateTime.now();
        Activation activation = Activation.builder() // TODO в маппер
                .user(user)
                .number(receiverActivationInfo.getNumber())
                .message(null)
                .country(country)
                .service(service)
                .source(source)
                .createDate(LocalDateTime.now())
                .finishDate(null)
                .plannedFinishDate(createDate.plusMinutes(minutesForActivation))
                .sourceId(receiverActivationInfo.getId())
                .status(ActivationStatus.ACTIVE.getCode())
                .cost(cost)
                .build();
        activation = activationRepository.save(activation);
        return mainMapper.mapActivationInfoFromActivation(activation);
    }
}
