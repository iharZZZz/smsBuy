package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum InternalStatus {
    OK(200, "ok");

    private final Integer statusCode;
    private final String statusVal;
}
