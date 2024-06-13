package cent.wong.compedia.app.interest.repository.impl;

import cent.wong.compedia.app.interest.repository.CompetitionInterestTypeRepository;
import cent.wong.compedia.entity.CompetitionInterestType;
import cent.wong.compedia.entity.dto.competition.GetCompetitionInterestTypeReq;
import cent.wong.json.JsonUtil;
import cent.wong.util.R2dbcQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CompetitionInterestTypeRepositoryImpl implements CompetitionInterestTypeRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private final R2dbcQueryUtil r2dbcQueryUtil;

    private final JsonUtil jsonUtil;

    @Override
    @CacheEvict(value = {
            "competition:interest:type:get",
            "competition:interest:type:getList"
    }, allEntries = true)
    public Mono<CompetitionInterestType> save(CompetitionInterestType competitionInterestType) {
        log.info("accepting competitionInterestType to be inserted: {}", jsonUtil.writeValueAsString(competitionInterestType));
        return r2dbcEntityTemplate.insert(competitionInterestType);
    }

    @Cacheable("competition:interest:type:get")
    @Override
    public Mono<CompetitionInterestType> get(GetCompetitionInterestTypeReq req) {
        log.info("accept get competitionInterestType req: {}", jsonUtil.writeValueAsString(req));
        Query query = this.r2dbcQueryUtil.generateQuery(req);
        return this.r2dbcEntityTemplate.selectOne(query, CompetitionInterestType.class);
    }

    @Cacheable("competition:interest:type:getList")
    @Override
    public Mono<List<CompetitionInterestType>> getList(GetCompetitionInterestTypeReq req) {
        log.info("accept getList competitionInterestType req: {}", jsonUtil.writeValueAsString(req));
        Query query = this.r2dbcQueryUtil.generateQuery(req);
        return this.r2dbcEntityTemplate.select(query, CompetitionInterestType.class).collectList();
    }
}
