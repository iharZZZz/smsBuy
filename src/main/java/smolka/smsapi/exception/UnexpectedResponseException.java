package smolka.smsapi.exception;

import lombok.Getter;
import org.springframework.http.ResponseEntity;
import smolka.smsapi.enums.ErrorDictionary;

@Getter
public class UnexpectedResponseException extends RuntimeException {

    private final ErrorDictionary error;
    private ResponseEntity<?> errorDetail;

    public UnexpectedResponseException(String message, ErrorDictionary error) {
        super(message);
        this.error = error;
    }

    public UnexpectedResponseException(String message, ErrorDictionary error, ResponseEntity<?> errorDetail) {
        super(message);
        this.error = error;
        this.errorDetail = errorDetail;
    }
}
