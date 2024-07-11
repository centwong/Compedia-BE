package cent.wong.compedia.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CompetitionPaidStatus {

    FREE(1L),
    NOT_PAID(2L),
    ON_REVIEW(3L),
    PAID(4L),
    FAILED_TO_PAY(5L);

    private final Long status;

    public static CompetitionPaidStatus getCompetitionPaidStatus(long status){
        for(CompetitionPaidStatus competitionPaidStatus: CompetitionPaidStatus.values()){
            if(competitionPaidStatus.status == status){
                return competitionPaidStatus;
            }
        }

        return null;
    }
}
