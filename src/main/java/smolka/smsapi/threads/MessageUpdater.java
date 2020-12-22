package smolka.smsapi.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.model.Activation;
import smolka.smsapi.service.activation.ActivationService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@Slf4j
public class MessageUpdater extends Thread {

    @Value(value = "${sms.api.update-message-checker-delay-secs}")
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
        log.error("Умер MessageUpdater");
        // TODO: здесь надо падать
    }

    private void step() {
        List<Activation> currentActivations = activationService.findAllInternalCurrentActivations();
        CommonReceiversActivationInfoMap receiverActivationInfoMap = activationService.getReceiversCurrentActivations();
        for (Activation activation : currentActivations) {
            ReceiverActivationStatusDto receiverActivationStatus = receiverActivationInfoMap.getActivation(activation.getSource(), activation.getSourceId());
            if (receiverActivationStatus != null) {
                if (receiverActivationStatus.getActivationStatus().equals(ActivationStatus.SMS_RECEIVED)
                        && receiverActivationStatus.getMessage() != null
                        && !receiverActivationStatus.getMessage().equals(activation.getMessage())) {
                    activationService.setMessageForActivation(activation, receiverActivationStatus.getMessage());
                }
            }
        }
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
