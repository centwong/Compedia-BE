package cent.wong.compedia.app.interest.repository;

import cent.wong.compedia.entity.CompetitionInterestType;
import cent.wong.compedia.entity.dto.competition.GetCompetitionInterestTypeReq;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CompetitionInterestTypeRepository {
    Mono<CompetitionInterestType> save(CompetitionInterestType competitionInterestType);
    Mono<CompetitionInterestType> get(GetCompetitionInterestTypeReq req);
    Mono<List<CompetitionInterestType>> getList(GetCompetitionInterestTypeReq req);
}
