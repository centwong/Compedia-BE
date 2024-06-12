package cent.wong.compedia.entity.dto.interest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SaveInterestReq implements Serializable {

    @NotNull(message = "FkInterestTypeIds should not null")
    private List<Long> fkInterestTypeIds;

    @NotNull(message = "FkInterestTimeIds should not null")
    private List<Long> fkInterestTimeIds;

    @NotNull(message = "FkInterestRangePriceIds should not null")
    private List<Long> fkInterestRangePriceIds;
}
