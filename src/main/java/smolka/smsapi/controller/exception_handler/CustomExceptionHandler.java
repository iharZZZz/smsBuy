package smolka.smsapi.controller.exception_handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.enums.ErrorDictionary;
import smolka.smsapi.enums.SmsConstants;
import smolka.smsapi.exception.InternalErrorException;

@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<ServiceMessage<String>> handleException(InternalErrorException exc) {
        logger.error("Exception in exception handler", exc);
        String errorMessage = exc.getError() != null ? exc.getError().getErrorMessage() : ErrorDictionary.UNKNOWN.getErrorMessage();
        ServiceMessage<String> serviceMessage = new ServiceMessage<>(SmsConstants.ERROR_STATUS.getValue(), errorMessage);
        return new ResponseEntity<>(serviceMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
