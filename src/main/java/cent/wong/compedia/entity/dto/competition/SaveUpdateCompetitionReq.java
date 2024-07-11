package cent.wong.compedia.entity.dto.competition;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SaveUpdateCompetitionReq implements Serializable {

    @NotNull(message = "Name should not null")
    @NotBlank(message = "Name should not empty")
    private String name;

    @NotNull(message = "InterestTypeId should not null")
    private List<Long> fkInterestTypeIds;

    @NotNull(message = "InterestTimeId should not null")
    private Long fkInterestTimeId;

    @NotNull(message = "UniversityId should not null")
    private Long fkUniversityId;

    @NotNull(message = "CompetitionFee should not null")
    private Long competitionFee;

    @NotNull(message = "CompetitionLevel should not null")
    @NotBlank(message = "CompetitionLevel should not empty")
    private String competitionLevel;

    @NotNull(message = "Price should not null")
    private Long price;

    @NotNull(message = "StartTime should not null")
    private Long startTime;

    @NotNull(message = "Deadline should not null")
    private Long deadline;

    @NotNull(message = "CityName should not null")
    private String cityName;

    @NotNull(message = "PublisherName should not null")
    private String publisherName;

    @NotNull(message = "PublisherEmail should not null")
    private String publisherEmail;

    private String publisherInstagram;

    @NotNull(message = "Description should not null")
    private String description;

    @NotNull(message = "LinkGuidebook should not null")
    private String linkGuidebook;

    @NotNull(message = "LinkCompetitionRegistration should not null")
    private String linkCompetitionRegistration;

    @NotNull(message = "PrizePool should not null")
    private Long prizePool;

    @NotNull(message = "You should define whether the competition is paid or not")
    private Boolean isRegistrationPaid;

    private Long paymentAmount;
}
