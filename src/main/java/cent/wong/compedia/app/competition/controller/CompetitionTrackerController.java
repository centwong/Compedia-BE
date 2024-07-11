package cent.wong.compedia.app.competition.controller;

import cent.wong.compedia.app.competition.repository.CompetitionRepository;
import cent.wong.compedia.app.competition.repository.CompetitionTrackerRepository;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.Competition;
import cent.wong.compedia.entity.CompetitionTracker;
import cent.wong.compedia.entity.dto.competition.GetCompetitionReq;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/public")
@Tag(name = "Competition Tracker")
@RequiredArgsConstructor
@Slf4j
public class CompetitionTrackerController {

    private final CompetitionTrackerRepository repository;

    private final CompetitionRepository competitionRepository;

    private final Tracer tracer;

    @PostMapping("/competition/tracker/{unique_id}")
    public Mono<ResponseEntity<BaseResponse<Competition>>> findByUniqueId(
            @PathVariable("unique_id") String uniqueId
    ){

        CompetitionTracker exampleBody = new CompetitionTracker();
        exampleBody.setUniqueId(uniqueId);

        return this.repository
                .findOne(
                        Example.of(exampleBody)
                )
                .flatMap((competitionTracker) -> {
                    GetCompetitionReq getReq = new GetCompetitionReq();
                    getReq.setId(competitionTracker.getId());
                    return this.competitionRepository.get(getReq);
                })
                .map((competition) -> BaseResponse.sendSuccess(tracer, competition))
                .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError((e) -> {
                    log.error("error occurred with message: {}", e);
                });
    }
}
