package smolka.smsapi.dto.input;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GetActivationHistoryRequest implements CustomPageableRequest {
    @NotNull
    private String userApiKey;

    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate startDateBefore;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate startDateAfter;

    private Integer page;
    private Integer pageSize;
}
