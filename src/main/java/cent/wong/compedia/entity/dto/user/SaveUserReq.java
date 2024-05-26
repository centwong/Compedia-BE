package cent.wong.compedia.entity.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
public class SaveUserReq implements Serializable {

    @NotNull(message = "Name should be exist")
    @NotBlank(message = "Name should not blank")
    @Length(min = 6, message = "Minimum name length is 6")
    private String name;

    @NotNull(message = "Password should be exist")
    @NotBlank(message = "Password should not blank")
    @Length(min = 6, message = "Minimum password length is 8")
    private String password;

    @NotNull(message = "Email should be exist")
    @NotBlank(message = "Email should not blank")
    @Email(message = "Should be an email")
    private String email;

    @NotNull(message = "FkUniversityId should be exist")
    private Long fkUniversityId;
}
