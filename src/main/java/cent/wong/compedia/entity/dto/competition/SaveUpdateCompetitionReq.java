package cent.wong.compedia.entity.dto.competition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SaveUpdateCompetitionReq implements Serializable {

    @NotNull(message = "Name should not null")
    @NotBlank(message = "Name should not empty")
    private String name;

    @NotNull(message = "InterestTypeId should not null")
    private List<Long> fkInterestTypeIds;

    @NotNull(message = "InterestTimeId should not null")
    private Long fkInterestTimeId;

    @NotNull(message = "Price should not null")
    private Long price;

    @NotNull(message = "Deadline should not null")
    private Long deadline;

    private String description;

    private String linkGuidebook;
}
