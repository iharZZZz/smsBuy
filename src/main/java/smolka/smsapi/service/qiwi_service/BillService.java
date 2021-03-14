package smolka.smsapi.service.qiwi_service;

import smolka.smsapi.dto.input.QiwiAddBalanceRequest;
import smolka.smsapi.dto.qiwi.CreateBillResponse;
import smolka.smsapi.exception.PaymentSystemException;
import smolka.smsapi.exception.UserNotFoundException;

public interface BillService {
    CreateBillResponse createBillAndReturnLink(QiwiAddBalanceRequest qiwiAddBalanceRequest) throws UserNotFoundException, PaymentSystemException;
}
