package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
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
import smolka.smsapi.model.UserKey;
import smolka.smsapi.repository.ActivationRepository;
import smolka.smsapi.repository.ActivationTargetRepository;
import smolka.smsapi.repository.CountryRepository;
import smolka.smsapi.service.activation.ActivationService;
import smolka.smsapi.service.api_key.ApiKeyService;
import smolka.smsapi.service.receiver.RestReceiver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivationServiceImpl implements ActivationService {

    private static final Integer MINUTES_FOR_ACTIVATION = 20;
    @Autowired
    private ActivationRepository activationRepository;
    @Autowired
    private ActivationTargetRepository activationTargetRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private ApiKeyService apiKeyService;
    @Autowired
    private RestReceiver smsHubReceiver;

    @Override
    @Transactional
    public ServiceMessage<ActivationInfoDto> orderActivation(String apiKey, BigDecimal cost, String serviceCode, String countryCode) {
        UserKey userKey = apiKeyService.findUserKey(apiKey);
        if (userKey == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        ActivationTarget service = activationTargetRepository.findByServiceCodeRepository(serviceCode);
        Country country = countryRepository.findByCountryCode(countryCode);
        CostMapDto costMap = mainMapper.mapToInternalCostMap(smsHubReceiver.getCostMap());
        if (!costMap.isExists(service.getServiceCode(), cost)) {
            throw new InternalErrorException("No numbers", ErrorDictionary.NO_NUMBER);
        }
        ReceiverActivationInfoDto receiverActivationInfo = smsHubReceiver.orderActivation(country, service);
        ActivationInfoDto activationInfo = this.createActivation(receiverActivationInfo, userKey, country, service, SourceList.SMSHUB, cost);
        return new ServiceMessage<>(InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), activationInfo);
    }

    @Override
    public ServiceMessage<ActivationStatusDto> getActivationForUser(String apiKey, Long id) {
        UserKey userKey = apiKeyService.findUserKey(apiKey);
        if (userKey == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        Activation activation = activationRepository.findActivationByIdAndUserKey(id, userKey);
        if (activation == null) {
            throw new InternalErrorException("This activation not exist", ErrorDictionary.NO_ACTIVATION);
        }
        ActivationStatusDto activationStatus = mainMapper.mapping(activation, ActivationStatusDto.class);
        return new ServiceMessage<>(InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), activationStatus);
    }

    @Override
    public ServiceMessage<ActivationsStatusDto> getActivationsForUser(String apiKey) {
        UserKey userKey = apiKeyService.findUserKey(apiKey);
        if (userKey == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        List<Activation> activations = activationRepository.findAllActivationsByUserKey(userKey);
        List<ActivationStatusDto> activationStatusList = mainMapper.toListMapping(activations, ActivationStatusDto.class);
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
        return activation;
    }

    @Override
    @Transactional
    public Activation succeedActivation(Activation activation) {
        activation.setStatus(ActivationStatus.SUCCEED.getCode());
        activation.setFinishDate(LocalDateTime.now());
        activation = activationRepository.save(activation);
        return activation;
    }

    @Override
    public List<Activation> findAllInternalCurrentActivations() {
        return activationRepository.findAllActivationsByStatus(ActivationStatus.ACTIVE.getCode());
    }

    @Override
    public List<Activation> findAllExpiredActivations() {
        return activationRepository.findAllActivationsByPlannedFinishDateLessThanEqual(LocalDateTime.now());
    }

    @Override
    public ReceiverActivationInfoMap getReceiversCurrentActivations() {
        ReceiverActivationInfoMap receiverActivationInfoMap = new ReceiverActivationInfoMap();
        List<ReceiverActivationStatusDto> receiverActivationStatusList = smsHubReceiver.getActivationsStatus();
        for (ReceiverActivationStatusDto receiverActivation : receiverActivationStatusList) {
            receiverActivationInfoMap.addActivationInfo(receiverActivation);
        }
        return receiverActivationInfoMap;
    }



    @Transactional
    private ActivationInfoDto createActivation(ReceiverActivationInfoDto receiverActivationInfo,
                                               UserKey userKey,
                                               Country country,
                                               ActivationTarget service,
                                               SourceList source,
                                               BigDecimal cost) {
        LocalDateTime createDate = LocalDateTime.now();
        Activation activation = Activation.builder()
                .userKey(userKey)
                .number(receiverActivationInfo.getNumber())
                .message(null)
                .country(country)
                .service(service)
                .source(source)
                .createDate(LocalDateTime.now())
                .finishDate(null)
                .plannedFinishDate(createDate.plusMinutes(MINUTES_FOR_ACTIVATION))
                .sourceId(receiverActivationInfo.getId())
                .status(ActivationStatus.ACTIVE.getCode())
                .cost(cost)
                .build();
        activation = activationRepository.save(activation);
        return mainMapper.mapping(activation, ActivationInfoDto.class);
    }
}
