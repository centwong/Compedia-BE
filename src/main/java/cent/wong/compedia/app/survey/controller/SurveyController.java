package cent.wong.compedia.app.survey.controller;

import cent.wong.compedia.app.survey.service.SurveyService;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.Persona;
import cent.wong.compedia.entity.dto.survey.CreatePersonaReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(description = "Get all survey question")
    @PostMapping("/v1/survey/question/get")
    public Mono<ResponseEntity<BaseResponse<Map<String, List<?>>>>> getSurveyQuestion(){
        return this.surveyService.getSurveyQuestion()
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Operation(description = "Save user persona")
    @PostMapping("/v1/persona/save")
    public Mono<ResponseEntity<BaseResponse<Persona>>> savePersona(
            Authentication authentication,
            @RequestBody @Valid CreatePersonaReq req
            ){
        return this.surveyService.savePersona(authentication, req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Operation(description = "Get persona of a user")
    @PostMapping("/v1/persona/get")
    public Mono<ResponseEntity<BaseResponse<Persona>>> getPersona(
            Authentication authentication
    ){
        return this.surveyService.get(authentication)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
