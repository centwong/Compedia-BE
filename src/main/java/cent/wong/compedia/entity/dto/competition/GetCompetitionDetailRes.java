package cent.wong.compedia.entity.dto.competition;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class GetCompetitionDetailRes implements Serializable {

    private Long id;

    private String name;

    private String deadline;

    private String location;

    private Long price;

    private String image;

    private List<String> type;

    private String publishedBy;

    private String description;

    private String universityName;

    private Map<String, String> winnerPrize;

    private String guidebookLink;
}
