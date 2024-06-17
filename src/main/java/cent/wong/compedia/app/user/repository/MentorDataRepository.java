package cent.wong.compedia.app.user.repository;

import cent.wong.compedia.entity.MentorData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MentorDataRepository extends R2dbcRepository<MentorData, Long> {

    @Query("SELECT * FROM mentor_data WHERE fk_user_id IN :id")
    Flux<MentorData> findByFkUserId(@Param("id") List<Long> ids);

    @Query("SELECT * FROM mentor_data WHERE fk_user_id = :id")
    Mono<MentorData> findByFkUserId(@Param("id") Long id);
}
