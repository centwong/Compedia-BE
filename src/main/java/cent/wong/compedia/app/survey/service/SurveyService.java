package cent.wong.compedia.app.survey.service;

import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.Persona;
import cent.wong.compedia.entity.dto.survey.CreatePersonaReq;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SurveyService {
    Mono<BaseResponse<Map<String, List<?>>>> getSurveyQuestion();
    Mono<BaseResponse<Persona>> savePersona(Authentication authentication, CreatePersonaReq req);
    Mono<BaseResponse<Persona>> get(Authentication authentication);
}
