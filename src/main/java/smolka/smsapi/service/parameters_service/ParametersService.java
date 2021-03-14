package smolka.smsapi.service.parameters_service;

public interface ParametersService {
    void savePercentageForMarkUpper(Integer percentage);

    void saveQiwiSecretKey(String qiwiSecretKey);

    Integer getPercentageForMarkUpper();

    String getQiwiSecretKey();
}
