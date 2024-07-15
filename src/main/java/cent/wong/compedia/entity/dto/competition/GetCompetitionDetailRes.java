package cent.wong.compedia.entity.dto.competition;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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

    private String winnerPrize;

    private String guidebookLink;

    private Long competitionFee;

    private String linkCompetitionRegistration;

    private Boolean isClosed;
}
