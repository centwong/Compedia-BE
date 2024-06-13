package cent.wong.compedia.app.survey.service.impl;

import cent.wong.compedia.app.interest.repository.InterestRepository;
import cent.wong.compedia.app.survey.service.SurveyService;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.InterestRangePrice;
import cent.wong.compedia.entity.InterestTime;
import cent.wong.compedia.entity.InterestType;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class SurveyServiceImpl implements SurveyService {

    private final Tracer tracer;

    private final InterestRepository interestRepository;

    @Override
    public Mono<BaseResponse<Map<String, List<?>>>> getSurveyQuestion() {

        Mono<List<InterestType>> interestType = this.interestRepository.getAllInterestType();
        Mono<List<InterestTime>> interestTime = this.interestRepository.getAllInterestTime();
        Mono<List<InterestRangePrice>> interestRangePrice = this.interestRepository.getAllInterestRangePrice();

        return Mono.zip(
                interestTime,
                interestType,
                interestRangePrice
        )
        .map((t3) -> {
            Map<String, List<?>> resMap = new LinkedHashMap<>();
            resMap.put(
                    "Halo calon juara! Kamu punya keminatan di background lomba apa aja? (bisa pilih lebih dari 1)",
                    t3.getT2()
            );
            resMap.put(
                    "Kamu prefer untuk mengikuti lomba yang sifat pelaksanaannya seperti apa? (bisa pilih lebih dari 1)",
                    t3.getT1()
            );
            resMap.put(
                    "Kamu prefer untuk mengikuti lomba yang harga pendaftarannya berada di range apa? (bisa pilih lebih dari 1)",
                    t3.getT3()
            );
            return BaseResponse.sendSuccess(tracer, resMap);
        })
        .onErrorResume((e) -> {
            log.error("error occurred with message: {}", e);;
            return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
        });
    }
}
