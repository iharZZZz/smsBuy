package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import smolka.smsapi.dto.qiwi.CreateBillRequest;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @GetMapping
    public CreateBillRequest create() {
        return CreateBillRequest.builder()
                .billId("1")
                .expirationDateTime(ZonedDateTime.now())
                .build();
    }
}
