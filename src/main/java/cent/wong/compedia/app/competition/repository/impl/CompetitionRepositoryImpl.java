package cent.wong.compedia.app.competition.repository.impl;

import cent.wong.compedia.app.competition.repository.CompetitionRepository;
import cent.wong.compedia.entity.Competition;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.competition.GetCompetitionReq;
import cent.wong.entity.Pagination;
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
public class CompetitionRepositoryImpl implements CompetitionRepository {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private final R2dbcQueryUtil r2dbcQueryUtil;

    private final JsonUtil jsonUtil;

    @CacheEvict(value = {
            "competition:get",
            "competition:getList"
    }, allEntries = true)
    @Override
    public Mono<Competition> save(Competition competition) {
        log.info("accepting competition data to be inserted: {}", jsonUtil.writeValueAsString(competition));
        return this.r2dbcEntityTemplate.insert(competition);
    }

    @Cacheable("competition:get")
    @Override
    public Mono<Competition> get(GetCompetitionReq req) {
        log.info("accepting get competition req: {}", jsonUtil.writeValueAsString(req));
        Query query = this.r2dbcQueryUtil.generateQuery(req);
        return this.r2dbcEntityTemplate.selectOne(query, Competition.class);
    }

    @Cacheable("competition:getList")
    @Override
    public Mono<PaginationRes<Competition>> getList(GetCompetitionReq req) {
        log.info("accepting getList competition req: {}", jsonUtil.writeValueAsString(req));

        Query queryList = this.r2dbcQueryUtil.generateQuery(req);
        Query queryCount = this.r2dbcQueryUtil.generateCount(req);

        Integer currentLimit = req.getPgParam().getLimit();
        Long currentOffset = req.getPgParam().getOffset();
        if(currentOffset != null && currentLimit != null){
            req.getPgParam().setOffset(
                    (currentOffset - 1) * currentLimit
            );
        }

        Mono<List<Competition>> getList = this.r2dbcEntityTemplate
                .select(queryList, Competition.class)
                .collectList();

        Mono<Long> count = this.r2dbcEntityTemplate.count(queryCount, Competition.class);

        return getList.zipWith(count)
                .map((t2) -> {
                    List<Competition> listData = t2.getT1();
                    Long numberOfRow = t2.getT2();

                    Pagination pg = new Pagination();
                    pg.setTotalElements(numberOfRow);
                    pg.setCurrentElements((long)listData.size());
                    pg.setCurrentPage(currentOffset);
                    pg = pg.processPagination(req.getPgParam(), pg);

                    return PaginationRes
                            .<Competition>builder()
                            .list(listData)
                            .pg(pg)
                            .build();
                });
    }

    @CacheEvict(value = {
            "competition:get",
            "competition:getList"
    }, allEntries = true)
    @Override
    public Mono<Object> update(Competition competition) {
        log.info("accepting competition data to be updated: {}", jsonUtil.writeValueAsString(competition));
        return this.r2dbcEntityTemplate.update(competition);
    }
}
