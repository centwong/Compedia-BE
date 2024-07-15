package cent.wong.compedia.entity.dto.competition;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GetCompetitionRes implements Serializable {

    private Long id;

    private String name;

    private String deadline;

    private String location;

    private Long price;

    private String daySinceDeadline;

    private String daySinceDeadlineColor;

    private String image;

    private List<String> type;

    private Boolean isClosed;
}
