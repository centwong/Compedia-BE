package cent.wong.compedia.entity.dto.competition;

import cent.wong.annotation.ParamColumn;
import cent.wong.entity.Pagination;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetCompetitionInterestTypeReq implements Serializable {

    @ParamColumn(name = "id")
    private Long id;

    @ParamColumn(name = "id")
    private List<Long> ids;

    @ParamColumn(name = "fk_competition_id")
    private Long fkCompetitionId;

    @ParamColumn(name = "fk_competition_id")
    private List<Long> fkCompetitionIds;

    @ParamColumn(name = "fk_interest_type_id")
    private Long fkInterestTypeId;

    @ParamColumn(name = "fk_interest_type_id")
    private List<Long> fkInterestTypeIds;

    private Pagination.PaginationParam pgParam = new Pagination.PaginationParam();
}
