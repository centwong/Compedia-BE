package cent.wong.compedia.entity.dto.mentor;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetMentorDataReq implements Serializable {

    private Long id;

    private Boolean isAccepted = false;

    private Boolean isWaiting = false;

    private Boolean isRejected = false;
}
