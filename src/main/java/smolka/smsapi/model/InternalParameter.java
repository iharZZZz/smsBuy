package smolka.smsapi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "PARAMS_DICT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InternalParameter {

    @Id
    @Column(name = "PARAM_NAME")
    private String name;

    @Column(name = "PARAM_VALUE")
    private String value;
}
