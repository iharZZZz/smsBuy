package smolka.smsapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import smolka.smsapi.enums.ActivationStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReceiverActivationStatusDto {
    private Long id;
    private ActivationStatus activationStatus;
    private String message;
    private LocalDateTime createDate;
}
