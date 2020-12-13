package smolka.smsapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorDictionary {
    WRONG_KEY(401, "Неверный API ключ"),
    WRONG_SERVICE(400, "Неизвестный сервис"),
    NO_BALANCE(403, "Не хватает денег на балансе"),
    NO_NUMBER(400, "В данный момент нет доступных номеров"),
    NO_ACTIVATION(400, "Такой активации нет"),
    UNKNOWN(500, "Неизвестная ошибка сервиса");


    private final Integer errorCode;
    private final String errorMessage;
}
