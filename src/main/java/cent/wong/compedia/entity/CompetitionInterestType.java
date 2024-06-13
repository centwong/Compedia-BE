package cent.wong.compedia.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
public class CompetitionInterestType implements Serializable {

    @Id
    private Long id;

    private Long fkCompetitionId;

    private Long fkInterestTypeId;
}
