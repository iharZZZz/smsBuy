package smolka.smsapi.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetActivationHistoryRequest extends ApiRequest implements CustomPageableRequest {

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate startDateBefore;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate startDateAfter;

    private Integer page;
    private Integer pageSize;
}
