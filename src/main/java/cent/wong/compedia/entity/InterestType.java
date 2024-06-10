package cent.wong.compedia.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
public class InterestType implements Serializable {

    @Column("id")
    private Long id;

    @Column("type")
    private String type;
}
