package smolka.smsapi.dto.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderRequest {
    @NotNull
    private String apiKey;
    @NotNull
    private BigDecimal cost;
    @NotNull
    private String service;
    @NotNull
    private String country;
}
