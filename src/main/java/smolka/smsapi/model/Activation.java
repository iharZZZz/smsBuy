package smolka.smsapi.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ACTIVATION")
@Data
@NamedEntityGraph(name = "activation.all", attributeNodes = {
        @NamedAttributeNode("userKey")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Activation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KEY_ID", referencedColumnName = "ID")
    private UserKey userKey;

    @Column(name = "NUMBER")
    private String number;

    @Column(name = "MESSAGE")
    private String message;

    @Column(name = "COUNTRY_CODE")
    private String countryCode;

    @Column(name = "SERVICE_CODE")
    private String serviceCode;

    @Column(name = "SOURCE_NAME")
    private String sourceName;

    @Column(name = "SOURCE_ID")
    private Long sourceId;

    @Column(name = "STATUS")
    private Integer status;

    @Column(name = "START_DATE")
    private LocalDateTime createDate;

    @Column(name = "CLOSE_DATE")
    private LocalDateTime finishDate;

    @Column(name = "COST")
    private BigDecimal cost;
}
