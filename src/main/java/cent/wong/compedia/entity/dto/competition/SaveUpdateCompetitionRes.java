package cent.wong.compedia.entity.dto.competition;

import lombok.Data;

import java.io.Serializable;

@Data
public class SaveUpdateCompetitionRes implements Serializable {
    private Long uniqueId;
    private String paymentId;
    private String paymentUri;
}
