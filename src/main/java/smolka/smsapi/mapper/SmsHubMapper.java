package smolka.smsapi.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import smolka.smsapi.dto.ReceiverActivationInfoDto;
import smolka.smsapi.dto.ReceiverActivationStatusDto;
import smolka.smsapi.dto.ReceiverCostMapDto;
import smolka.smsapi.enums.ActivationStatus;
import smolka.smsapi.enums.ServiceList;
import smolka.smsapi.enums.smshub.SmsHubServiceDictionary;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SmsHubMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<ReceiverActivationStatusDto> mapActivationsStatusResponse(Map<String, Object> root) {
        List<Map<String, Object>> arrayActivations = (List<Map<String, Object>>)root.get("array");
        List<ReceiverActivationStatusDto> activationStatusList = new ArrayList<>();
        for (Map<String, Object> element : arrayActivations) {
            final Long id = Long.parseLong(element.get("id").toString());
            ActivationStatus status = ActivationStatus.ACTIVE;
            int statusCode = Integer.parseInt(element.get("status").toString());
            switch (statusCode) {
                case 3: {
                    status = ActivationStatus.SMS_RECEIVED;
                }
            }
            final String code = element.get("code").toString();
            final LocalDateTime createDate = LocalDateTime.parse(element.get("createDate").toString());
            ReceiverActivationStatusDto activationStatus = new ReceiverActivationStatusDto(id, status, code, createDate);
            activationStatusList.add(activationStatus);
        }
        return activationStatusList;
    }

    public Map<String, Object> getMapActivationsStatus(String response) throws JsonProcessingException {
        return objectMapper.readValue(response, HashMap.class);
    }

    public ReceiverActivationInfoDto extractActivationInfoFromResponse(String response) {
        final String activationNumber = response.substring(response.lastIndexOf(":") + 1);
        final Long activationId = Long.parseLong(response.substring(response.indexOf(":") + 1, response.lastIndexOf(":")));
        return new ReceiverActivationInfoDto(activationId, activationNumber);
    }

    public ReceiverCostMapDto mapCostMapForSmsHubJson(String response) throws JsonProcessingException {
        Map<String, Object> json = objectMapper.readValue(response, HashMap.class);
        ReceiverCostMapDto costMap = new ReceiverCostMapDto();
        final String priceMapKey = "priceMap";
        for (String s : json.keySet()) {
            ServiceList service = SmsHubServiceDictionary.getInternalServiceBySmsHubName(s);
            if (service != null) {
                Map<String, Integer> serviceCosts = (Map<String, Integer>) ((Map<String, Object>)json.get(s)).get(priceMapKey);
                for (String cost : serviceCosts.keySet()) {
                    costMap.addCostToMap(service, new BigDecimal(cost), serviceCosts.get(cost));
                }
            }
        }
        return costMap;
    }
}
