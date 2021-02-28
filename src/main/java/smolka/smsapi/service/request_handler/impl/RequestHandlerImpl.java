package smolka.smsapi.service.request_handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.dto.input.*;
import smolka.smsapi.enums.Action;
import smolka.smsapi.exception.*;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.activation.CurrentActivationService;
import smolka.smsapi.service.api_key.UserService;
import smolka.smsapi.service.request_handler.RequestHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Service
public class RequestHandlerImpl implements RequestHandler {

    @Autowired
    private ActivationHistoryService activationHistoryService;

    @Autowired
    private CurrentActivationService currentActivationService;

    @Autowired
    private UserService userService;

    @Autowired
    private Validator validator;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ServiceMessage<?> handle(String requestBody) throws UserNotFoundException, UserBalanceIsEmptyException, ReceiverException, NoNumbersException, ActivationNotFoundException {
        try {
            ApiRequest apiRequest = objectMapper.readValue(requestBody, ApiRequest.class);
            validate(apiRequest);
            Action action = Action.getAction(apiRequest.getAction());
            if (action == null) {
                throw new ValidationException("Неизвестная операция");
            }
            switch (action) {
                case ORDER: {
                    OrderRequest orderRequest = objectMapper.readValue(requestBody, OrderRequest.class);
                    validate(orderRequest);
                    return currentActivationService.orderActivation(orderRequest);
                }
                case COST: {
                    GetCostRequest getCostRequest = objectMapper.readValue(requestBody, GetCostRequest.class);
                    return currentActivationService.getCostsForActivations(getCostRequest);
                }
                case ACTIVATION_HISTORY: {
                    GetActivationHistoryRequest getActivationHistoryRequest = objectMapper.readValue(requestBody, GetActivationHistoryRequest.class);
                    return activationHistoryService.getActivationHistorySortedByFinishDateDesc(getActivationHistoryRequest);
                }
                case USER_INFO: {
                    return userService.getUserInfo(apiRequest.getApiKey());
                }
                case CURR_ACTIVATION: {
                    GetActivationRequest getActivationRequest = objectMapper.readValue(requestBody, GetActivationRequest.class);
                    validate(getActivationRequest);
                    return currentActivationService.getCurrentActivationForUser(getActivationRequest);
                }
                case CURR_ACTIVATIONS: {
                    return currentActivationService.getCurrentActivationsForUser(apiRequest.getApiKey());
                }
                default: {
                    throw new ValidationException("Неизвестная операция");
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private <T extends ApiRequest> void validate(T request) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            ConstraintViolation<T> firstViolation = violations.stream().findFirst().orElse(null);
            throw new ValidationException(firstViolation.getMessage());
        }
    }
}
