package cent.wong.compedia.entity.dto.interest;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SaveInterestReq implements Serializable {

    @NotNull(message = "fkInterestTypeIds should not null")
    private List<Long> fkInterestTypeIds;

    @NotNull(message = "fkInterestTimeIds should not null")
    private List<Long> fkInterestTimeIds;

    @NotNull(message = "fkInterestRangePriceIds should not null")
    private List<Long> fkInterestRangePriceIds;
}
