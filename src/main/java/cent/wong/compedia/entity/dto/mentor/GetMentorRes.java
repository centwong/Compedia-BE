package cent.wong.compedia.entity.dto.mentor;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetMentorRes implements Serializable {

    private Long userId;

    private List<String> type;

    private String name;

    private String achievement;
}
