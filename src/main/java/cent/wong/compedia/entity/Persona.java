package cent.wong.compedia.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
public class Persona implements Serializable {

    @Id
    @Column("id")
    private Long id;

    @Column("fk_user_id")
    private Long fkUserId;

    @Column("persona")
    private String persona;
}
