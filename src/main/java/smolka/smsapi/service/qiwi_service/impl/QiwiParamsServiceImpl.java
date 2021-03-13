package smolka.smsapi.service.qiwi_service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smolka.smsapi.model.InternalParameter;
import smolka.smsapi.repository.ParametersRepository;
import smolka.smsapi.service.qiwi_service.QiwiParamsService;

@Service
@Slf4j
public class QiwiParamsServiceImpl implements QiwiParamsService {

    @Autowired
    private ParametersRepository parametersRepository;

    private static final String QIWI_SECRET_KEY_PARAMETER_NAME = "SECRET_KEY_QIWI";

    @Override
    @Transactional
    public void updateSecretKey(String key) {
        if (key == null) {
            throw new IllegalArgumentException("Невозможно установить для SECRET_KEY нулловое значение");
        }
        InternalParameter qiwiParameter = parametersRepository.findByName(QiwiParamsServiceImpl.QIWI_SECRET_KEY_PARAMETER_NAME);
        if (qiwiParameter == null) {
            throw new RuntimeException("QIWI параметр " + QiwiParamsServiceImpl.QIWI_SECRET_KEY_PARAMETER_NAME + " не существует");
        }
        qiwiParameter.setValue(key);
        parametersRepository.save(qiwiParameter);
    }

    @Override
    public String getSecretKey() {
        InternalParameter qiwiParameter = parametersRepository.findByName(QIWI_SECRET_KEY_PARAMETER_NAME);
        if (qiwiParameter == null || qiwiParameter.getValue() == null) {
            throw new RuntimeException("QIWI параметр " + QIWI_SECRET_KEY_PARAMETER_NAME + " не существует");
        }
        return qiwiParameter.getValue();
    }
}
