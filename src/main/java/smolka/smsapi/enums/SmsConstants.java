package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SmsConstants {
    ERROR_STATUS("ERROR"),
    SUCCESS_STATUS("SUCCESS");

    String value;
}
