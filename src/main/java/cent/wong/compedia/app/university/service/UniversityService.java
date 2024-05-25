package cent.wong.compedia.app.university.service;

import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.University;
import cent.wong.compedia.entity.dto.university.GetUniversityReq;
import reactor.core.publisher.Mono;

public interface UniversityService {
    Mono<BaseResponse<University>> get(GetUniversityReq req);
    Mono<BaseResponse<PaginationRes<University>>> getList(GetUniversityReq req);
}
