package cent.wong.compedia.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
public class Interest implements Serializable {

    @Column("id")
    private Long id;

    @Column("fk_user_id")
    private Long fkUserId;

    @Column("fk_interest_type_ids")
    private String fkInterestTypeIds;

    @Column("fk_interest_time_ids")
    private String fkInterestTimeIds;

    @Column("fk_interest_range_price_ids")
    private String fkInterestRangePriceIds;
}
