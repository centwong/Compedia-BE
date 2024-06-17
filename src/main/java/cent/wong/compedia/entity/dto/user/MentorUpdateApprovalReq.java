package cent.wong.compedia.entity.dto.user;

import cent.wong.compedia.constant.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class MentorUpdateApprovalReq implements Serializable {

    @NotNull(message = "UserId should be exist")
    private Long mentorDataId;

    @NotNull(message = "Status should be exist")
    private ApprovalStatus status;

}
