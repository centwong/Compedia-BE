package cent.wong.compedia.entity.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginUserRes implements Serializable {

    private Long id;

    private String name;

    private String jwtToken;

    private Boolean isAlreadyFillInterestSurvey;

    private Boolean isAlreadyFillPersonaSurvey;
}
