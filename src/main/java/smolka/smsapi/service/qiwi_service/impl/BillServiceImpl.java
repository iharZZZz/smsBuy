package smolka.smsapi.service.qiwi_service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import smolka.smsapi.dto.input.QiwiAddBalanceRequest;
import smolka.smsapi.dto.qiwi.CreateBillRequest;
import smolka.smsapi.dto.qiwi.CreateBillResponse;
import smolka.smsapi.exception.PaymentSystemException;
import smolka.smsapi.exception.UserNotFoundException;
import smolka.smsapi.mapper.MainMapper;
import smolka.smsapi.model.QiwiBill;
import smolka.smsapi.model.User;
import smolka.smsapi.repository.QiwiBillRepository;
import smolka.smsapi.repository.UserRepository;
import smolka.smsapi.service.parameters_service.ParametersService;
import smolka.smsapi.service.qiwi_service.BillService;
import smolka.smsapi.utils.QiwiBillRequestCreator;

@Service
public class BillServiceImpl implements BillService {

    private static final String QIWI_BILL_URL = "https://api.qiwi.com/partner/bill/v1/bills/%s";
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ParametersService parametersService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QiwiBillRepository qiwiBillRepository;
    @Autowired
    private MainMapper mainMapper;
    @Value("${sms.api.bill_life_minutes}")
    private Integer billLifeMinutes;

    @Override
    @Transactional(rollbackFor = { UserNotFoundException.class, PaymentSystemException.class })
    public CreateBillResponse createBillAndReturnLink(QiwiAddBalanceRequest qiwiAddBalanceRequest) throws UserNotFoundException, PaymentSystemException {
        User user = userRepository.findUserByKey(qiwiAddBalanceRequest.getApiKey());
        if (user == null) {
            throw new UserNotFoundException("Данного юзера не существует!");
        }
        QiwiBill qiwiBill = mainMapper.mapQiwiBillFromAddBalanceRequestAndUser(qiwiAddBalanceRequest, user);
        qiwiBill = qiwiBillRepository.save(qiwiBill);
        CreateBillRequest createBillRequest = mainMapper.mapQiwiBillRequestFromEntity(qiwiBill, billLifeMinutes);
        return getBillResponse(createBillRequest, qiwiBill.getId());
    }

    private CreateBillResponse getBillResponse(CreateBillRequest createBillRequest, String billId) throws PaymentSystemException {
        ResponseEntity<String> response = null;
        try {
            HttpEntity<CreateBillRequest> request = QiwiBillRequestCreator.createBillRequest(createBillRequest, parametersService.getQiwiSecretKey());
            response = restTemplate.exchange(String.format(QIWI_BILL_URL, billId), HttpMethod.PUT, request, String.class);
            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new Exception("ошибка при общении с QIWI ");
            }
            return mainMapper.mapping(response.getBody(), CreateBillResponse.class);
        } catch (Exception exc) {
            throw new PaymentSystemException("ошибка при общении с QIWI ", response);
        }
    }
}
