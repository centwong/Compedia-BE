package cent.wong.compedia.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;
import java.util.List;

@Data
public class Competition implements Serializable {

    @Id
    @Column("id")
    private Long id;

    @Column("image_id")
    private String imageId;

    @Column("image")
    private String image;

    @Column("name")
    private String name;

    @Transient
    private List<Long> fkInterestTypeId;

    @JsonIgnore
    @Column("fk_interest_type_ids")
    private String fkInterestTypeIds;

    @Column("fk_user_id")
    private Long fkUserId;

    @Column("price")
    private Long price;

    @Column("deadline")
    private Long deadline;

    @Column("fk_interest_time_id")
    private Long fkInterestTimeId;

    @Column("description")
    private String description;

    @Column("link_guidebook")
    private String linkGuidebook;

    @Column("created_at")
    private Long createdAt;

    @Column("created_by")
    private Long createdBy;

    @Column("updated_at")
    private Long updatedAt;

    @Column("updated_by")
    private Long updatedBy;

    @Column("deleted_at")
    private Long deletedAt;

    @Column("deleted_by")
    private Long deletedBy;

    @Column("is_active")
    private Boolean isActive;
}
