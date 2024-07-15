package cent.wong.compedia.entity.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class MentorUpdateDetailReq implements Serializable {

    @NotNull(message = "JobTitle should not null")
    @NotBlank(message = "JobTitle should not empty")
    private String jobTitle;

    @NotNull(message = "MentoringScope should not null")
    @NotBlank(message = "MentoringScope should not empty")
    private String mentoringScope;

    @NotNull(message = "TncDescription should not null")
    @NotBlank(message = "TncDescription should not empty")
    private String tncDescription;
}
