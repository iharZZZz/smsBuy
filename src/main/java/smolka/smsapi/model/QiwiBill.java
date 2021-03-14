package smolka.smsapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "QIWI_BILL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QiwiBill {

    @Id
    @Column(name = "ID")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID")
    private User user;

    @Column(name = "AMOUNT")
    private BigDecimal amount;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "CLOSE_DATE")
    private LocalDateTime closeDate;

    @Column(name = "STATUS")
    private Integer status;
}
