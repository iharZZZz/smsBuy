package smolka.smsapi.dto.qiwi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBillResponse {
    private String billId;
    private String payUrl;
}
