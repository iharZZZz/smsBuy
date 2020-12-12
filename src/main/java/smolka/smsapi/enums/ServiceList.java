package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceList {
    VK("vk"),
    OK("ok"),
    AVITO("av"),
    INSTAGRAM("in");

    private final String serviceName;
}
