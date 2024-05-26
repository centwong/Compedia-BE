package cent.wong.compedia.entity.dto.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserReq implements Serializable {

    @NotNull(message = "Email should exist")
    private String email;

    @NotNull(message = "Password should exist")
    private String password;
}
