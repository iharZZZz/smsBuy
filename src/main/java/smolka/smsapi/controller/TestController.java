package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.input.GetActivationHistoryRequest;
import smolka.smsapi.dto.input.OrderRequest;
import smolka.smsapi.exception.*;
import smolka.smsapi.service.activation.ActivationHistoryService;
import smolka.smsapi.service.activation.CurrentActivationService;
import smolka.smsapi.service.api_key.UserService;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private ActivationHistoryService activationHistoryService;

    @Autowired
    private CurrentActivationService currentActivationService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ServiceMessage<CurrentActivationCreateInfoDto> order(@RequestBody @Validated OrderRequest orderRequest) throws ReceiverException, UserNotFoundException, NoNumbersException, UserBalanceIsEmptyException {
//        userService.testSaveUser(orderRequest.getApiKey());
        return currentActivationService.orderActivation(orderRequest.getApiKey(), orderRequest.getCost(), orderRequest.getService(), orderRequest.getCountry());
    }

    @GetMapping("/cost")
    public ServiceMessage<CostMapDto> getCostMap(@RequestParam("apiKey") String apiKey, @RequestParam(required = false) String country) throws ReceiverException, UserNotFoundException {
//        userService.testSaveUser(apiKey);
        return currentActivationService.getCostsForActivations(apiKey, country);
    }

    @PostMapping("/getActivationHistory")
    public ServiceMessage<ActivationHistoryDto> getActivationHistory(@RequestBody GetActivationHistoryRequest getActivationHistoryRequest) throws UserNotFoundException {
        return activationHistoryService.getActivationHistorySortedByFinishDateDesc(getActivationHistoryRequest);
    }

    @GetMapping("/user")
    public ServiceMessage<UserDto> getUserInfo(@RequestParam("apiKey") String apiKey) throws UserNotFoundException {
//        userService.testSaveUser(apiKey);
        return userService.getUserInfo(apiKey);
    }

    @GetMapping("/currentActivationStatus")
    public ServiceMessage<ActivationMessageDto> getActivationStatus(@RequestParam("apiKey") String apiKey, @RequestParam("id") Long id) throws ActivationNotFoundException, UserNotFoundException {
//        userService.testSaveUser(apiKey);
        return currentActivationService.getCurrentActivationForUser(apiKey, id);
    }

    @GetMapping("/allCurrentActivations")
    public ServiceMessage<CurrentActivationsStatusDto> getAllCurrentActivations(@RequestParam("apiKey") String apiKey) throws UserNotFoundException {
//        userService.testSaveUser(apiKey);
        return currentActivationService.getCurrentActivationsForUser(apiKey);
    }
}
