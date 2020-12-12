package smolka.smsapi.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import smolka.smsapi.enums.CountryList;
import smolka.smsapi.enums.ServiceList;
import smolka.smsapi.enums.smshub.SmsHubCountryDictionary;
import smolka.smsapi.enums.smshub.SmsHubServiceDictionary;

@UtilityClass
public class SmsHubRequestCreator {

    private static final String API_KEY_FORM_DATA_PROP_NAME = "api_key";
    private static final String ACTION_FORM_DATA_PROP_NAME = "action";
    private static final String SERVICE_FORM_DATA_PROP_NAME = "service";
    private static final String OPERATOR_FORM_DATA_PROP_NAME = "operator";
    private static final String COUNTRY_FORM_DATA_PROP_NAME = "country";

    public static HttpEntity<MultiValueMap<String, String>> createOrderRequest(String apiKey, String action, CountryList country, ServiceList service) {
        final String operator = "any";
        final SmsHubCountryDictionary smsHubCountry = SmsHubCountryDictionary.getSmsHubCountryByInternalCountry(country);
        if (smsHubCountry == null) {
            throw new IllegalArgumentException("Такая страна не поддерживается");
        }
        final SmsHubServiceDictionary smsHubService = SmsHubServiceDictionary.getSmsHubServiceByInternalService(service);
        if (smsHubService == null) {
            throw new IllegalArgumentException("Такой сервис не поддерживается");
        }
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(API_KEY_FORM_DATA_PROP_NAME, apiKey);
        map.add(ACTION_FORM_DATA_PROP_NAME, action);
        map.add(SERVICE_FORM_DATA_PROP_NAME, smsHubService.getSmsHubName());
        map.add(OPERATOR_FORM_DATA_PROP_NAME, operator);
        map.add(COUNTRY_FORM_DATA_PROP_NAME, smsHubCountry.getSmsHubCountryName());
        return new HttpEntity<>(map, httpHeaders);
    }

    public static HttpEntity<MultiValueMap<String, String>> createEmptyRequest(String apiKey, String action) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add(API_KEY_FORM_DATA_PROP_NAME, apiKey);
        map.add(ACTION_FORM_DATA_PROP_NAME, action);
        return new HttpEntity<>(map, httpHeaders);
    }
}
