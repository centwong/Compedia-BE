package cent.wong.compedia.app.interest.repository;

import cent.wong.compedia.entity.Interest;
import cent.wong.compedia.entity.InterestRangePrice;
import cent.wong.compedia.entity.InterestTime;
import cent.wong.compedia.entity.InterestType;
import cent.wong.compedia.entity.dto.interest.GetInterestReq;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InterestRepository {
    Mono<Interest> save(Interest interest);
    Mono<Interest> get(GetInterestReq req);
    Mono<List<InterestType>> getAllInterestType();
    Mono<List<InterestTime>> getAllInterestTime();
    Mono<List<InterestRangePrice>> getAllInterestRangePrice();
}
