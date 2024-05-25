package cent.wong.compedia.app.university.service.impl;

import cent.wong.compedia.app.university.repository.UniversityRepository;
import cent.wong.compedia.app.university.service.UniversityService;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.University;
import cent.wong.compedia.entity.dto.university.GetUniversityReq;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UniversityServiceImpl implements UniversityService {

    private final UniversityRepository repository;

    private final Tracer tracer;


    @Cacheable("university:get")
    @Override
    public Mono<BaseResponse<University>> get(GetUniversityReq req) {
        return this.repository.get(req)
                .map((d) -> {
                    return BaseResponse.sendSuccess(tracer, d);
                })
                .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Cacheable("university:getList")
    @Override
    public Mono<BaseResponse<PaginationRes<University>>> getList(GetUniversityReq req) {
        return this.repository.getList(req)
                .map((d) -> {
                    return BaseResponse.sendSuccess(tracer, d);
                })
                .switchIfEmpty(
                        Mono.defer(() -> {
                            PaginationRes<University> pgRes = PaginationRes.<University>builder().build();
                            return Mono.just(BaseResponse.sendSuccess(tracer, pgRes));
                        })
                )
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }
}
