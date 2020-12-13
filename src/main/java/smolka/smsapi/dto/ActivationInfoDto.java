package smolka.smsapi.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivationInfoDto {
    private Long id;
    private String number;
    private String countryCode;
    private String serviceCode;
    private Integer status;
    private LocalDateTime createDate;
    private BigDecimal cost;
}
