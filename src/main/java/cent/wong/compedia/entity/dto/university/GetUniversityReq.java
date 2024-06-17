package cent.wong.compedia.entity.dto.university;

import cent.wong.annotation.ParamColumn;
import cent.wong.entity.Pagination;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetUniversityReq implements Serializable {

    @ParamColumn(name = "id")
    private Long id;

    @ParamColumn(name = "id")
    private List<Long> ids;

    @ParamColumn(name = "name")
    private String name;

    @ParamColumn(name = "name")
    private List<String> names;

    private Pagination.PaginationParam pgParam = new Pagination.PaginationParam();
}
