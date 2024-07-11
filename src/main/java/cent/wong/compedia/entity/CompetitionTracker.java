package cent.wong.compedia.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
public class CompetitionTracker implements Serializable {

    @Id
    @Column("id")
    private Long id;

    @Column("competition_id")
    private Long competitionId;

    @Column("unique_id")
    private String uniqueId;
}
