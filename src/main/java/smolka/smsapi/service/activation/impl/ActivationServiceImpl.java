package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.receiver.ReceiverActivationInfoDto;
import smolka.smsapi.enums.*;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.Activation;
import smolka.smsapi.model.UserKey;
import smolka.smsapi.repository.ActivationRepository;
import smolka.smsapi.service.activation.ActivationService;
import smolka.smsapi.service.api_key.ApiKeyService;
import smolka.smsapi.service.receiver.RestReceiver;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivationServiceImpl implements ActivationService {

    @Autowired
    private ActivationRepository activationRepository;
    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private ApiKeyService apiKeyService;
    @Autowired
    private RestReceiver smsHubReceiver;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ServiceMessage<ActivationInfoDto> orderActivation(String apiKey, BigDecimal cost, ServiceList service, CountryList country) {
        UserKey userKey = apiKeyService.findUserKey(apiKey);
        if (userKey == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        CostMapDto costMap = mainMapper.mapToInternalCostMap(smsHubReceiver.getCostMap());
        if (!costMap.isExists(service, cost)) {
            throw new InternalErrorException("No numbers", ErrorDictionary.NO_NUMBER);
        }
        ReceiverActivationInfoDto receiverActivationInfo = smsHubReceiver.orderActivation(country, service);
        ActivationInfoDto activationInfo = this.createActivation(receiverActivationInfo, userKey, country, service, SourceList.SMSHUB, cost);
        return new ServiceMessage<>(InternalStatus.OK.getStatusCode(), InternalStatus.OK.getStatusVal(), activationInfo);
    }

    @Override
    public ServiceMessage<ActivationStatusDto> getActivation(String apiKey, Long id) {
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
    public ServiceMessage<ActivationsStatusDto> getActivations(String apiKey) {
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
    public void succeedActivation(Long id, String message) {
        Activation activation = activationRepository.findActivationById(id);
        if (activation == null) {
            throw new InternalErrorException("This activation not exists!");
        }
        activation.setFinishDate(LocalDateTime.now());
        activation.setStatus(ActivationStatus.SUCCEED.getCode());
        activation.setMessage(message);
        activationRepository.save(activation);
    }

    @Override
    @Transactional
    public void closeActivation(Long id) {
        Activation activation = activationRepository.findActivationById(id);
        if (activation == null) {
            throw new InternalErrorException("This activation not exists!");
        }
        activation.setFinishDate(LocalDateTime.now());
        activation.setStatus(ActivationStatus.CLOSED.getCode());
        activationRepository.save(activation);
    }

    @Transactional
    private ActivationInfoDto createActivation(ReceiverActivationInfoDto receiverActivationInfo,
                                               UserKey userKey,
                                               CountryList country,
                                               ServiceList service,
                                               SourceList source,
                                               BigDecimal cost) {
        Activation activation = Activation.builder()
                .userKey(userKey)
                .number(receiverActivationInfo.getNumber())
                .message(null)
                .countryCode(country.getCountryName())
                .serviceCode(service.getServiceName())
                .sourceName(source.toString())
                .createDate(LocalDateTime.now())
                .finishDate(null)
                .sourceId(receiverActivationInfo.getId())
                .status(ActivationStatus.ACTIVE.getCode())
                .cost(cost)
                .build();
        activation = activationRepository.save(activation);
        return mainMapper.mapping(activation, ActivationInfoDto.class);
    }
}
