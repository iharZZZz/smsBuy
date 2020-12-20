package smolka.smsapi.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.model.Activation;
import smolka.smsapi.service.activation.ActivationService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class ActivationStatusUpdater extends Thread {

    @Value(value = "${sms.api.update-status-delay-secs}")
    private Integer delay;

    @Autowired
    private ActivationService activationService;

    @PostConstruct
    public void initialize() {
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(delay * 1000);
                step();
            }
            catch (Exception e) {
                log.error("Ошибка в MessageUpdater ", e);
                break;
            }
        }
        log.error("Умер ActivationStatusUpdater");
        // TODO: здесь надо падать
    }

    private void step() {
        List<Activation> expiredActivations = activationService.findAllExpiredActivations();
        for (Activation expiredActivation : expiredActivations) {
            if (expiredActivation.getStatus().equals(ActivationStatus.SMS_RECEIVED.getCode())) {
                activationService.succeedActivation(expiredActivation);
            } else {
                activationService.closeActivation(expiredActivation);
            }
        }
    }
}
