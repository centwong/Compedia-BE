package cent.wong.compedia.entity;

import lombok.Data;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
public class InterestRangePrice implements Serializable {

    @Column("id")
    private Long id;

    @Column("range_price")
    private String rangePrice;

    @Column("lower_offset")
    private Long lowerOffset;

    @Column("upper_offset")
    private Long upperOffset;
}
