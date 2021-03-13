package smolka.smsapi.service.qiwi_service;

import smolka.smsapi.dto.qiwi.CreateBillResponse;

public interface BillService {
    CreateBillResponse createBillAndReturnLink();
}
