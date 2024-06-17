package cent.wong.compedia.app.user.repository;

import cent.wong.compedia.entity.MentorInterestType;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

public interface MentorInterestTypeRepository extends R2dbcRepository<MentorInterestType, Long> {

    @Query("SELECT * FROM mentor_interest_type WHERE fk_user_id = :id")
    Flux<MentorInterestType> findByFkUserId(@Param("id") Long id);
}
