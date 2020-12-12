package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import smolka.smsapi.dto.ReceiverMessage;

@Getter
@AllArgsConstructor
public enum ErrorDictionary {
    WRONG_KEY(401, "Неверный API ключ"),
    WRONG_SERVICE(400, "Неизвестный сервис"),
    NO_BALANCE(403, "Не хватает денег на балансе"),
    NO_NUMBER(400, "В данный момент нет доступных номеров"),
    UNKNOWN(500, "Неизвестная ошибка сервиса");


    private final Integer errorCode;
    private final String errorMessage;

    public <T> ReceiverMessage<T> extractMessage(Class<T> cls) {
        return new ReceiverMessage<>(cls, errorCode, errorMessage, null);
    }
}
