package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import smolka.smsapi.dto.*;
import smolka.smsapi.dto.input.OrderDto;
import smolka.smsapi.service.activation.ActivationService;
import smolka.smsapi.service.api_key.UserService;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private ActivationService activationService;

    @Autowired
    private UserService userService;

    @PostMapping("/order")
    public ServiceMessage<ActivationInfoDto> order(@RequestBody @Validated OrderDto orderDto) {
        return activationService.orderActivation(orderDto.getApiKey(), orderDto.getCost(), orderDto.getService(), orderDto.getCountry());
    }

    @GetMapping("/user")
    public ServiceMessage<UserDto> getUserInfo(@RequestParam("apiKey") String apiKey) {
        return userService.getUserInfo(apiKey);
    }

    @GetMapping("/activationStatus")
    public ServiceMessage<ActivationStatusDto> getActivationStatus(@RequestParam("apiKey") String apiKey, @RequestParam("id") Long id) {
        return activationService.getActivationForUser(apiKey, id);
    }

    @GetMapping("/allActivations")
    public ServiceMessage<ActivationsStatusDto> getAllCurrentActivations(@RequestParam("apiKey") String apiKey) {
        return activationService.getCurrentActivationsForUser(apiKey);
    }
}
