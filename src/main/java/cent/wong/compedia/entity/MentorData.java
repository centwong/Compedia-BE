package cent.wong.compedia.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;

import java.io.Serializable;

@Data
public class MentorData implements Serializable {

    @Id
    @Column("id")
    private Long id;

    @Column("fk_user_id")
    private Long fkUserId;

    @Column("achievement")
    private String achievement;

    @Column("description")
    private String description;

    @Column("linkedin_url")
    private String linkedinUrl;

    @Column("instagram_url")
    private String instagramUrl;

    @Column("job_title")
    private String jobTitle;

    @Column("mentoring_scope")
    private String mentoringScope;

    @Column("tnc_description")
    private String tncDescription;

    @Column("approval_status")
    private Long approvalStatus;

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
}
