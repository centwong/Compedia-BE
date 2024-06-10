package cent.wong.compedia.app.interest.service;

import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.InterestRangePrice;
import cent.wong.compedia.entity.InterestTime;
import cent.wong.compedia.entity.InterestType;
import cent.wong.compedia.entity.dto.interest.SaveInterestReq;
import cent.wong.compedia.entity.dto.interest.SaveInterestRes;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.List;

public interface InterestService {
    Mono<BaseResponse<SaveInterestRes>> save(Authentication auth, SaveInterestReq req);
    Mono<BaseResponse<List<InterestType>>> getAllInterestType();
    Mono<BaseResponse<List<InterestTime>>> getAllInterestTime();
    Mono<BaseResponse<List<InterestRangePrice>>> getAllInterestRangePrice();
}
