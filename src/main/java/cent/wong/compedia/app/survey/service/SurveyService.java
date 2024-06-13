package cent.wong.compedia.app.survey.service;

import cent.wong.compedia.entity.BaseResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SurveyService {
    Mono<BaseResponse<Map<String, List<?>>>> getSurveyQuestion();
}
