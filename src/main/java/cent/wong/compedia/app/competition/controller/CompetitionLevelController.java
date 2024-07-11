package cent.wong.compedia.app.competition.controller;

import cent.wong.compedia.entity.BaseResponse;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Tag(name = "CompetitionLevel")
@RequestMapping("/public")
@RequiredArgsConstructor
public class CompetitionLevelController {

    private final Tracer tracer;

    @Operation(description = "Get competition levels")
    @PostMapping("/v1/get/competition/level")
    public Mono<ResponseEntity<BaseResponse<List<String>>>> getCompetitionLevels(){
        return Mono.just(
                ResponseEntity.ok(
                        BaseResponse.sendSuccess(
                                tracer,
                                List.of(
                                        "Kota",
                                        "Provinsi",
                                        "Nasional",
                                        "Internasional"
                                )
                        )
                )
        );
    }
}
