package cent.wong.compedia.entity.dto.interest;

import cent.wong.annotation.ParamColumn;
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
public class GetInterestReq implements Serializable {

    @ParamColumn(name = "id")
    private Long id;

    @ParamColumn(name = "id")
    private List<Long> ids;

    @ParamColumn(name = "fk_user_id")
    private Long fkUserId;

    @ParamColumn(name = "fk_user_id")
    private List<Long> fkUserIds;
}
