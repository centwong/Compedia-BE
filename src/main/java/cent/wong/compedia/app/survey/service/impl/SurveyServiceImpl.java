package cent.wong.compedia.app.survey.service.impl;

import cent.wong.compedia.app.ai.AIService;
import cent.wong.compedia.app.interest.repository.InterestRepository;
import cent.wong.compedia.app.survey.repository.PersonaRepository;
import cent.wong.compedia.app.survey.service.SurveyService;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.*;
import cent.wong.compedia.entity.dto.survey.CreatePersonaReq;
import cent.wong.compedia.util.AuthenticationUtil;
import cent.wong.json.JsonUtil;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
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

    private final PersonaRepository personaRepository;

    private final AIService aiService;

    private final JsonUtil jsonUtil;

    private final AuthenticationUtil authenticationUtil;

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

    @CacheEvict(
            value = "persona:get",
            allEntries = true
    )
    @Override
    public Mono<BaseResponse<Persona>> savePersona(Authentication authentication, CreatePersonaReq req) {
        String strReq = jsonUtil.writeValueAsString(req);
        log.info("accepting save persona req: {}", strReq);
        Long id = authenticationUtil.extractId(authentication);

        // make the example to fetched first
        Persona getPersonaReq = new Persona();
        getPersonaReq.setFkUserId(id);
        Example<Persona> examplePersona = Example.of(
                getPersonaReq
        );

        return this.personaRepository
                .findOne(
                        examplePersona
                )
                .flatMap((getPersona) -> {
                    PersonaEnum newPersona = this.aiService.anaylzePersona(strReq);
                    getPersona.setPersona(newPersona.getPersona());
                    return this.personaRepository.save(getPersona);
                })
                .switchIfEmpty(Mono.fromCallable(() -> {
                    PersonaEnum personaEnum = this.aiService.anaylzePersona(strReq);
                    Persona persona = new Persona();
                    persona.setPersona(personaEnum.getPersona());
                    persona.setFkUserId(id);

                    return this.personaRepository.save(persona);
                }).flatMap((d) -> d))
                .map((savedPersona) -> BaseResponse.sendSuccess(tracer, savedPersona))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);;
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
                });
    }

    @Cacheable("persona:get")
    @Override
    public Mono<BaseResponse<Persona>> get(Authentication authentication) {
        Long userId = authenticationUtil.extractId(authentication);
        log.info("accepting get persona from userId: {}", userId);

        // make the example to fetched first
        Persona getPersonaReq = new Persona();
        getPersonaReq.setFkUserId(userId);
        Example<Persona> examplePersona = Example.of(
                getPersonaReq
        );

        return this.personaRepository.findOne(examplePersona)
                .map((persona) -> BaseResponse.sendSuccess(tracer, persona))
                .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
                });
    }
}
