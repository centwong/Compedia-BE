package cent.wong.compedia.entity.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetUserRes implements Serializable {

    private Long id;

    private String email;

    private String name;

    private Integer role;

    private Long fkUniversityId;

    private String domain;
}
