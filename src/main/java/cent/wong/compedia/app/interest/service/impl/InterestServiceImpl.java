package cent.wong.compedia.app.interest.service.impl;

import cent.wong.compedia.app.interest.repository.InterestRepository;
import cent.wong.compedia.app.interest.service.InterestService;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.*;
import cent.wong.compedia.entity.dto.interest.SaveInterestReq;
import cent.wong.compedia.entity.dto.interest.SaveInterestRes;
import cent.wong.compedia.util.AuthenticationUtil;
import cent.wong.json.JsonUtil;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class InterestServiceImpl implements InterestService {

    private final InterestRepository interestRepository;

    private final AuthenticationUtil authenticationUtil;

    private final JsonUtil jsonUtil;

    private final Tracer tracer;

    @Override
    public Mono<BaseResponse<SaveInterestRes>> save(Authentication auth, SaveInterestReq req) {
        Interest interest = new Interest();
        interest.setFkUserId(authenticationUtil.extractId(auth));
        interest.setFkInterestTimeIds(jsonUtil.writeValueAsString(req.getFkInterestTimeIds()));
        interest.setFkInterestTypeIds(jsonUtil.writeValueAsString(req.getFkInterestTypeIds()));
        interest.setFkInterestRangePriceIds(jsonUtil.writeValueAsString(req.getFkInterestRangePriceIds()));
        return this.interestRepository.save(interest)
                .map((res) -> BaseResponse.<SaveInterestRes>sendSuccess(tracer))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);;
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<List<InterestType>>> getAllInterestType() {
        return this.interestRepository.getAllInterestType()
                .map((data) -> BaseResponse.sendSuccess(tracer, data))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);;
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<List<InterestTime>>> getAllInterestTime() {
        return this.interestRepository.getAllInterestTime()
                .map((data) -> BaseResponse.sendSuccess(tracer, data))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);;
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<List<InterestRangePrice>>> getAllInterestRangePrice() {
        return this.interestRepository.getAllInterestRangePrice()
                .map((data) -> BaseResponse.sendSuccess(tracer, data))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);;
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
                });
    }
}
