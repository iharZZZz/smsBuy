package smolka.smsapi.threads;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.CommonReceiversActivationInfoMap;
import smolka.smsapi.dto.receiver.ReceiverActivationStatusDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.model.Activation;
import smolka.smsapi.model.User;
import smolka.smsapi.service.activation.ActivationService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private void setMessageForActivationsStep() {
        List<Activation> currentActivations = activationService.findAllInternalActiveActivations();
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
    }

    private void closeExpiredActivationsStep() {
        Map<User, List<Activation>> expiredActivationsPerUser = activationService.findAllExpiredActivationsForUsers();
        for (User user : expiredActivationsPerUser.keySet()) {
            List<Activation> allExpiredActivations = expiredActivationsPerUser.get(user);
            List<Activation> activationsForClose = new ArrayList<>();
            List<Activation> activationsForSucceed = new ArrayList<>();
            allExpiredActivations.forEach(a -> {
                if (a.getStatus().equals(ActivationStatus.SMS_RECEIVED.getCode())) {
                    activationsForSucceed.add(a);
                } else {
                    activationsForClose.add(a);
                }
            });
            activationService.succeedActivationsForUser(user, activationsForSucceed);
            activationService.closeActivationsForUser(user, activationsForClose);
        }
    }

    private void step() {
        setMessageForActivationsStep();
        closeExpiredActivationsStep();
    }
}
