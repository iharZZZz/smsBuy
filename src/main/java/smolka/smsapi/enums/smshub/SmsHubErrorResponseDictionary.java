package smolka.smsapi.enums.smshub;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smolka.smsapi.enums.ErrorDictionary;

import java.util.Arrays;

@AllArgsConstructor
@Getter
public enum SmsHubErrorResponseDictionary {
    SMSHUB_NO_KEY("NO_KEY", ErrorDictionary.WRONG_KEY),
    SMSHUB_BAD_KEY("BAD_KEY", ErrorDictionary.WRONG_KEY),
    SMSHUB_ERROR_SQL("ERROR_SQL", ErrorDictionary.UNKNOWN),
    SMSHUB_BAD_ACTION("BAD_ACTION", ErrorDictionary.UNKNOWN),
    SMSHUB_NO_NUMBERS("NO_NUMBERS", ErrorDictionary.NO_NUMBER),
    SMSHUB_NO_BALANCE("NO_BALANCE", ErrorDictionary.NO_NUMBER),
    SMSHUB_WRONG_SERVICE("WRONG_SERVICE", ErrorDictionary.UNKNOWN),
    SMSHUB_BAD_SERVICE("BAD_SERVICE", ErrorDictionary.UNKNOWN),
    SMSHUB_NO_ACTIVATION("NO_ACTIVATION", ErrorDictionary.UNKNOWN);

    private final String smsHubErrorMsg;
    private final ErrorDictionary error;

    public static boolean isError(String response) {
        SmsHubErrorResponseDictionary dictResponse = Arrays.stream(values())
                .filter(e -> e.getSmsHubErrorMsg().equals(response))
                .findFirst()
                .orElse(null);
        return dictResponse != null;
    }

    public static ErrorDictionary getError(String response) {
        return Arrays.stream(values())
                .filter(e -> e.getSmsHubErrorMsg().equals(response))
                .findFirst()
                .map(e -> e.error)
                .orElse(ErrorDictionary.UNKNOWN);
    }
}
