package smolka.smsapi.utils;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import smolka.smsapi.dto.qiwi.CreateBillRequest;

@UtilityClass
public class QiwiBillRequestCreator {


    public static HttpEntity<CreateBillRequest> createBillRequest(CreateBillRequest createBillRequest, String authHeaderToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBearerAuth(authHeaderToken);
        return new HttpEntity<>(createBillRequest, httpHeaders);
    }
}
