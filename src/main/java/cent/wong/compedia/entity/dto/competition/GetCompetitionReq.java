package cent.wong.compedia.entity.dto.competition;

import cent.wong.annotation.ParamColumn;
import cent.wong.entity.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetCompetitionReq implements Serializable {

    @ParamColumn(name = "id")
    private Long id;

    @ParamColumn(name = "id")
    private List<Long> ids;

    @ParamColumn(name = "fk_interest_type_ids")
    private Long fkInterestTypeId;

    @ParamColumn(name = "fk_interest_type_ids")
    private List<Long> fkInterestTypeIds;

    @ParamColumn(name = "fk_interest_time_id")
    private Long fkInterestTimeId;

    @ParamColumn(name = "fk_interest_time_id")
    private List<Long> fkInterestTimeIds;

    @ParamColumn(name = "is_active")
    private Boolean isActive;

    private Pagination.PaginationParam pgParam = new Pagination.PaginationParam();
}
