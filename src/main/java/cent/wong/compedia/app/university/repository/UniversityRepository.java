package cent.wong.compedia.app.university.repository;

import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.University;
import cent.wong.compedia.entity.dto.university.GetUniversityReq;
import reactor.core.publisher.Mono;

public interface UniversityRepository {
    Mono<University> get(GetUniversityReq req);
    Mono<PaginationRes<University>> getList(GetUniversityReq req);
}
