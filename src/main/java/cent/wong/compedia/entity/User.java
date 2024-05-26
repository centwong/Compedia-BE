package cent.wong.compedia.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @Column("id")
    private Long id;

    @Column("email")
    private String email;

    @Column("password")
    private String password;

    @Column("name")
    private String name;

    @Column("role")
    private Integer role;

    @Column("fk_university_id")
    private Long fkUniversityId;

    @Column("domain")
    private String domain;

    private Long createdAt = new Date().getTime();

    private Long updatedAt;

    private Long deletedAt;

    private Boolean isActive = true;
}
