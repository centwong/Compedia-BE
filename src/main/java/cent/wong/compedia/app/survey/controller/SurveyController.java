package cent.wong.compedia.app.survey.controller;

import cent.wong.compedia.app.survey.service.SurveyService;
import cent.wong.compedia.entity.BaseResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/private")
@Tag(name = "Survey")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
public class SurveyController {

    private final SurveyService surveyService;

    @PostMapping("/v1/survey/question/get")
    public Mono<ResponseEntity<BaseResponse<Map<String, List<?>>>>> getSurveyQuestion(){
        return this.surveyService.getSurveyQuestion()
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
