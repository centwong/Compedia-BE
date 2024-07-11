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

//    @Column("fk_user_id")
//    private Long fkUserId;

    @Column("price")
    private Long price;

    @Column("start_time")
    private Long startTime;

    @Column("deadline")
    private Long deadline;

    @Column("fk_interest_time_id")
    private Long fkInterestTimeId;

    @Column("fk_university_id")
    private Long fkUniversityId;

    @Column("competition_fee")
    private Long competitionFee;

    @Column("description")
    private String description;

    @Column("prize_pool")
    private String prizePool;

    @Column("link_guidebook")
    private String linkGuidebook;

    @Column("link_competition_registration")
    private String linkCompetitionRegistration;

    @Column("publisher_name")
    private String publisherName;

    @Column("publisher_email")
    private String publisherEmail;

    @Column("publisher_instagram")
    private String publisherInstagram;

    @Column("competition_level")
    private String competitionLevel;

    @Column("city_name")
    private String cityName;

    @Column("competition_paid_status")
    private Long competitionPaidStatus;

    @Column("created_at")
    private Long createdAt;

//    @Column("created_by")
//    private Long createdBy;

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
