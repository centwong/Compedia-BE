package cent.wong.compedia.entity.dto.user;

import cent.wong.annotation.ParamColumn;
import cent.wong.entity.Pagination;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;
import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUserReq implements Serializable {

    @ParamColumn(name = "id")
    private Long id;

    @ParamColumn(name = "id")
    private List<Long> ids;

    @ParamColumn(name = "email")
    private String email;

    @ParamColumn(name = "email")
    private List<String> emails;

    @ParamColumn(name = "fk_university_id")
    private Long fkUniversityId;

    @ParamColumn(name = "fk_university_id")
    private List<Long> fkUniversityIds;

    @JsonIgnore
    @ParamColumn(name = "role")
    private Integer role;

    @JsonIgnore
    @ParamColumn(name = "role")
    private List<Integer> roles;

    @ParamColumn(name = "is_active")
    @JsonIgnore
    private Boolean isActive;

    private Pagination.PaginationParam pgParam = new Pagination.PaginationParam();
}
