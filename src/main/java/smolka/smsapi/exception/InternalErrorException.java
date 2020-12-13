package smolka.smsapi.exception;

import smolka.smsapi.enums.ErrorDictionary;

public class InternalErrorException extends RuntimeException {

    private ErrorDictionary error;

    public InternalErrorException(String message) {
        super(message);
    }

    public InternalErrorException(String message, ErrorDictionary error) {
        super(message);
        this.error = error;
    }
}
