package smolka.smsapi.enums.smshub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smolka.smsapi.enums.ServiceList;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum SmsHubServiceDictionary {
    SMSHUB_VK(ServiceList.VK, "vk"),
    SMSHUB_OK(ServiceList.OK, "ok"),
    SMSHUB_AVITO(ServiceList.AVITO, "av"),
    SMSHUB_INSTAGRAM(ServiceList.INSTAGRAM, "in");

    private final ServiceList service;
    private final String smsHubName;

    public static ServiceList getInternalServiceBySmsHubName(String smsHubName) {
        return Arrays.stream(values())
                .filter(e -> e.smsHubName.equals(smsHubName))
                .findFirst()
                .map(SmsHubServiceDictionary::getService)
                .orElse(null);
    }

    public static SmsHubServiceDictionary getSmsHubServiceByInternalService(ServiceList service) {
        return Arrays.stream(values())
                .filter(e -> e.service.equals(service))
                .findFirst()
                .orElse(null);
    }
}
