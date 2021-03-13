package smolka.smsapi.dto.qiwi;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBillRequest {
    private String billId;
    private AmountDto amount;
    @JsonFormat(pattern = "YYYY-MM-DDTHH:mm:ss")
    private LocalDateTime expirationDateTime;
}
