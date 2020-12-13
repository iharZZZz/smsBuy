package smolka.smsapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @GetMapping("/test")
    public void test() {
        String a = "1";
        String b = "1";
        if (a == b) {
            System.out.println("rr");
        }
    }
}
