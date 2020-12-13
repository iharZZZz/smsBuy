package smolka.smsapi.dto.receiver;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverActivationStatusListDto {
    private List<ReceiverActivationStatusDto> activations;
}
