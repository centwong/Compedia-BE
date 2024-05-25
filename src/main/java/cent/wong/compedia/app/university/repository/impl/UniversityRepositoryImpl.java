package cent.wong.compedia.app.university.repository.impl;

import cent.wong.compedia.app.university.repository.UniversityRepository;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.University;
import cent.wong.compedia.entity.dto.university.GetUniversityReq;
import cent.wong.entity.Pagination;
import cent.wong.json.JsonUtil;
import cent.wong.util.R2dbcQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class UniversityRepositoryImpl implements UniversityRepository {

    private final R2dbcQueryUtil r2dbcQueryUtil;

    private final JsonUtil jsonUtil;

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Mono<University> get(GetUniversityReq req) {
        log.info("accepting get university req: {}", jsonUtil.writeValueAsString(req));

        req.getPgParam().setLimit(1);

        Query query = r2dbcQueryUtil.generateQuery(req);

        return this.r2dbcEntityTemplate
                .selectOne(query, University.class)
                .doOnNext((d) -> log.info("success fetch university: {}", jsonUtil.writeValueAsString(d)));
    }

    @Override
    public Mono<PaginationRes<University>> getList(GetUniversityReq req) {
        log.info("accepting getList university req: {}", jsonUtil.writeValueAsString(req));

        Long currentPage = req.getPgParam().getOffset();
        if(currentPage != null && req.getPgParam().getLimit() != null){
            req.getPgParam().setOffset((currentPage - 1) * req.getPgParam().getLimit());
        }

        Query queryFetch = this.r2dbcQueryUtil.generateQuery(req);
        Query queryCount = this.r2dbcQueryUtil.generateCount(req);

        Mono<List<University>> fetchUniversity = this.r2dbcEntityTemplate
                .select(queryFetch, University.class)
                .collectList()
                .doOnNext((d) -> log.info("success fetch list of university: {}", jsonUtil.writeValueAsString(d)));

        Mono<Long> fetchCount = this.r2dbcEntityTemplate.count(queryCount, University.class)
                .doOnNext((d) -> log.info("success fetch count university: {}", jsonUtil.writeValueAsString(d)));

        return fetchUniversity.zipWith(fetchCount)
                .map((t2) -> {
                    Pagination pg = new Pagination();
                    pg.setTotalElements(t2.getT2());
                    pg.setCurrentPage(currentPage);
                    pg.setCurrentElements((long)t2.getT1().size());
                    pg = pg.processPagination(req.getPgParam(), pg);

                    return PaginationRes
                            .<University>builder()
                            .list(t2.getT1())
                            .pg(pg)
                            .build();
                });
    }
}
