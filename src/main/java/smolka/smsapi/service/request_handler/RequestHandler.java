package smolka.smsapi.service.request_handler;

import smolka.smsapi.dto.ServiceMessage;
import smolka.smsapi.exception.*;

public interface RequestHandler {
    ServiceMessage<?> handle(String requestBody) throws UserNotFoundException, UserBalanceIsEmptyException, ReceiverException, NoNumbersException, ActivationNotFoundException;
}
