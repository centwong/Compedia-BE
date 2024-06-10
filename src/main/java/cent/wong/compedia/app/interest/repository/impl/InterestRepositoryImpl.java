package cent.wong.compedia.app.interest.repository.impl;

import cent.wong.compedia.app.interest.repository.InterestRepository;
import cent.wong.compedia.entity.Interest;
import cent.wong.compedia.entity.InterestRangePrice;
import cent.wong.compedia.entity.InterestTime;
import cent.wong.compedia.entity.InterestType;
import cent.wong.compedia.entity.dto.interest.GetInterestReq;
import cent.wong.util.R2dbcQueryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class InterestRepositoryImpl implements InterestRepository {

    private final R2dbcQueryUtil r2dbcQueryUtil;

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Override
    public Mono<Interest> save(Interest interest) {
        return this.r2dbcEntityTemplate
                .insert(interest);
    }

    @Override
    public Mono<Interest> get(GetInterestReq req) {
        Query query = this.r2dbcQueryUtil.generateQuery(req);
        return this.r2dbcEntityTemplate.selectOne(query, Interest.class);
    }

    @Cacheable("interest:type:*")
    @Override
    public Mono<List<InterestType>> getAllInterestType() {
        return this.r2dbcEntityTemplate
                .select(InterestType.class)
                .all()
                .collectList();
    }

    @Cacheable("interest:time:*")
    @Override
    public Mono<List<InterestTime>> getAllInterestTime() {
        return this.r2dbcEntityTemplate
                .select(InterestTime.class)
                .all()
                .collectList();
    }

    @Cacheable("interest:range:price:*")
    @Override
    public Mono<List<InterestRangePrice>> getAllInterestRangePrice() {
        return this.r2dbcEntityTemplate
                .select(InterestRangePrice.class)
                .all()
                .collectList();
    }
}
