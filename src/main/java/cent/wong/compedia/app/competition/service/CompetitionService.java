package cent.wong.compedia.app.competition.service;

import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.competition.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import reactor.core.publisher.Mono;

public interface CompetitionService {
    Mono<BaseResponse<SaveUpdateCompetitionRes>> save(Authentication authentication, SaveUpdateCompetitionReq req, Mono<FilePart> file);
    Mono<BaseResponse<GetCompetitionDetailRes>> get(GetCompetitionReq req);
    Mono<BaseResponse<PaginationRes<GetCompetitionRes>>> getList(GetCompetitionReq req);
    Mono<BaseResponse<Object>> update(Authentication authentication, GetCompetitionReq getCompetitionReq, SaveUpdateCompetitionReq req, Mono<FilePart> file);
    Mono<BaseResponse<Object>> delete(Authentication authentication, GetCompetitionReq req);
    Mono<BaseResponse<Object>> activate(Authentication authentication, GetCompetitionReq req);
}
