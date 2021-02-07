package smolka.smsapi.service.activation.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.dto.ActivationHistoryDto;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.input.GetActivationHistoryRequest;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.enums.ErrorDictionary;
import smolka.smsapi.enums.SmsConstants;
import smolka.smsapi.enums.SortDictionary;
import smolka.smsapi.exception.InternalErrorException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.ActivationHistory;
import smolka.smsapi.model.CurrentActivation;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.ActivationHistoryRepository;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.api_key.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivationHistoryServiceImpl implements ActivationHistoryService {

    @Autowired
    private MainMapper mainMapper;
    @Autowired
    private ActivationHistoryRepository activationHistoryRepository;
    @Autowired
    private UserService userService;

    @Override
    public ServiceMessage<ActivationHistoryDto> getActivationHistorySortedByFinishDateDesc(GetActivationHistoryRequest getActivationHistoryRequest) {
        User user = userService.findUserByUserKey(getActivationHistoryRequest.getUserApiKey());
        if (user == null) {
            throw new InternalErrorException("Api key not exists", ErrorDictionary.WRONG_KEY);
        }
        Pageable pageableForActivationHistoryWithSortFinishDateAsc = mainMapper.mapPageableFromCustomPageRequest(getActivationHistoryRequest, SortDictionary.ACTIVATION_HISTORY_SORT_FINISH_DATE_DESC);
        Page<ActivationHistory> activationHistory = activationHistoryRepository.findAllByUser(pageableForActivationHistoryWithSortFinishDateAsc, user);
        ActivationHistoryDto activationHistoryDto = mainMapper.mapActivationHistoryDtoFromActivationHistoryList(activationHistory.toList());
        return new ServiceMessage<>(SmsConstants.SUCCESS_STATUS.getValue(), activationHistoryDto);
    }

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

    @Override
    public ActivationHistory findActivationHistoryById(Long id) {
        return activationHistoryRepository.findById(id).orElse(null);
    }
}
