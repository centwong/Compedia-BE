package cent.wong.compedia.entity.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SaveMentorDataReq implements Serializable {

    @NotNull(message = "FkInterestTypeIds should exist")
    private List<Long> fkInterestTypeIds;

    @NotNull(message = "Description should exist")
    @NotBlank(message = "Description should not blank")
    private String description;

    @NotNull(message = "Achievement should exist")
    @NotBlank(message = "Achievement should not blank")
    private String achievement;

    @NotNull(message = "Linkedin url should exist")
    @NotBlank(message = "Linkedin url should not blank")
    private String linkedinUrl;

    @NotNull(message = "Instagram url should exist")
    @NotBlank(message = "Instagram url should not blank")
    private String instagramUrl;
}
