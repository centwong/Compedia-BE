package cent.wong.compedia.entity.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateUserReq implements Serializable {

    private String name;

    private String password;

    @JsonIgnore
    private Boolean isVerified;

    @JsonIgnore
    private Long deletedAt;

    @JsonIgnore
    private Boolean isActive;
}
