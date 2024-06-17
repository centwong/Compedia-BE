package cent.wong.compedia.entity.dto.survey;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class CreatePersonaReq implements Serializable {

    @NotNull(message = "Motivation should exist")
    @NotBlank(message = "Motivation should not blank")
    private String motivation;

    @NotNull(message = "Problem want to be solved should exist")
    @NotBlank(message = "Problem want to be solved should not blank")
    private String problemWantToSolved;

    @NotNull(message = "Competition experience should exist")
    @NotBlank(message = "Competition experience should not blank")
    private String competitionExperience;

    @NotNull(message = "Total winning competition should exist")
    @NotBlank(message = "Total winning competition should not blank")
    private String totalWinCompetition;

    @NotNull(message = "Name of the winning competition should exist")
    @NotBlank(message = "Name of the winning competition should not blank")
    private String nameOfWinningCompetition;
}
