package smolka.smsapi.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetActivationHistoryRequest implements CustomPageableRequest {
    @NotNull
    private String userApiKey;
    private Integer page;
    private Integer pageSize;
}
