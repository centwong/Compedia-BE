package cent.wong.compedia.app.competition.repository;

import cent.wong.compedia.entity.Competition;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.competition.GetCompetitionReq;
import reactor.core.publisher.Mono;

public interface CompetitionRepository {
    Mono<Competition> save(Competition competition);
    Mono<Competition> get(GetCompetitionReq req);
    Mono<PaginationRes<Competition>> getList(GetCompetitionReq req);
    Mono<Object> update(Competition competition);
}
