package cent.wong.compedia.entity.dto.university;

import cent.wong.annotation.ParamColumn;
import cent.wong.entity.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetUniversityReq implements Serializable {

    @ParamColumn(name = "name")
    private String name;

    @ParamColumn(name = "name")
    private List<String> names;

    private Pagination.PaginationParam pgParam = new Pagination.PaginationParam();
}
