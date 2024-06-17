package cent.wong.compedia.entity.dto.mentor;

import cent.wong.annotation.ParamColumn;
import cent.wong.entity.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetMentorReq implements Serializable {

    @ParamColumn(name = "id")
    private Long id;

    @ParamColumn(name = "id")
    private List<Long> ids;

    @ParamColumn(name = "role")
    private Integer role;

    @ParamColumn(name = "roles")
    private List<Integer> roles;

    @ParamColumn(name = "is_active")
    private Boolean isActive;

    private Pagination.PaginationParam pgParam = new Pagination.PaginationParam();
}
