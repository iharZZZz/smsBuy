package smolka.smsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ReceiverMessage<T> {
    @JsonIgnore
    private Class<T> type;
    private Integer statusCode;
    private String status;
    private T payload;

    @JsonIgnore
    public Boolean isSuccess() {
        return statusCode != null && statusCode.equals(200);
    }

    @JsonIgnore
    public Boolean isError() {
        return !(isSuccess());
    }
}
