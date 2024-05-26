package cent.wong.compedia.app.user.repository.impl;

import cent.wong.compedia.app.user.repository.UserRepository;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.User;
import cent.wong.compedia.entity.dto.user.GetUserReq;
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
@Slf4j
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final R2dbcQueryUtil r2dbcQueryUtil;

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    private final JsonUtil jsonUtil;

    @CacheEvict(value = {
        "user:get",
        "user:getList"
    }, allEntries = true)
    @Override
    public Mono<User> save(User user) {
        log.info("accepting insert user data: {}", jsonUtil.writeValueAsString(user));
        return this.r2dbcEntityTemplate.insert(user);
    }

    @Cacheable("user:get")
    @Override
    public Mono<User> get(GetUserReq req) {
        log.info("accepting getUser req: {}", jsonUtil.writeValueAsString(req));

        req.getPgParam().setLimit(1);

        Query query = this.r2dbcQueryUtil.generateQuery(req);
        return this.r2dbcEntityTemplate.selectOne(query, User.class)
                .doOnNext((d) -> log.info("success fetch user: {}", jsonUtil.writeValueAsString(d)));
    }

    @Cacheable("user:getList")
    @Override
    public Mono<PaginationRes<User>> getList(GetUserReq req) {
        log.info("accepting getList user req: {}", jsonUtil.writeValueAsString(req));

        Long currentOffset = req.getPgParam().getOffset();
        Integer currentLimit = req.getPgParam().getLimit();
        if(currentOffset != null && currentLimit != null){
            req.getPgParam().setOffset((currentOffset - 1) * currentLimit);
        }

        Query queryUser = this.r2dbcQueryUtil.generateQuery(req);
        Query queryCount = this.r2dbcQueryUtil.generateQuery(req);

        Mono<List<User>> fetchUser = this.r2dbcEntityTemplate.select(queryUser, User.class)
                .collectList()
                .doOnNext((d) -> log.info("success fetch data from user: {}", jsonUtil.writeValueAsString(d)));

        Mono<Long> fetchCount = this.r2dbcEntityTemplate.count(queryCount, User.class)
                .doOnNext((d) -> log.info("success fetch count from user: {}", d));

        return fetchUser.zipWith(fetchCount)
                .map((t2) -> {
                    List<User> list = t2.getT1();
                    Long totalData = t2.getT2();

                    Pagination pg = new Pagination();
                    pg.setCurrentElements((long)list.size());
                    pg.setCurrentPage(currentOffset);
                    pg.setTotalElements(totalData);
                    pg = pg.processPagination(req.getPgParam(), pg);

                    return PaginationRes
                            .<User>builder()
                            .pg(pg)
                            .list(list)
                            .build();
                });
    }

    @CacheEvict(value = {
            "user:get",
            "user:getList"
    }, allEntries = true)
    @Override
    public Mono<?> update(User user) {
        log.info("accept update user data: {}", jsonUtil.writeValueAsString(user));
        return this.r2dbcEntityTemplate.update(user);
    }
}
