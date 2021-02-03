package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.input.OrderDto;
import smolka.smsapi.service.activation.CurrentActivationService;
import smolka.smsapi.service.api_key.UserService;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private CurrentActivationService currentActivationService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ServiceMessage<ActivationInfoDto> order(@RequestBody @Validated OrderDto orderDto) {
        return currentActivationService.orderActivation(orderDto.getApiKey(), orderDto.getCost(), orderDto.getService(), orderDto.getCountry());
    }

    @GetMapping("/cost")
    public CostMapDto getCostMap(@RequestParam("apiKey") String apiKey) {
        return currentActivationService.getCostsForActivations(apiKey);
    }

    @GetMapping("/user")
    public ServiceMessage<UserDto> getUserInfo(@RequestParam("apiKey") String apiKey) {
        return userService.getUserInfo(apiKey);
    }

    @GetMapping("/currentActivationStatus")
    public ServiceMessage<ActivationStatusDto> getActivationStatus(@RequestParam("apiKey") String apiKey, @RequestParam("id") Long id) {
        return currentActivationService.getCurrentActivationForUser(apiKey, id);
    }

    @GetMapping("/allCurrentActivations")
    public ServiceMessage<ActivationsStatusDto> getAllCurrentActivations(@RequestParam("apiKey") String apiKey) {
        return currentActivationService.getCurrentActivationsForUser(apiKey);
    }
}
