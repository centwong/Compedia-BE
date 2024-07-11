package cent.wong.compedia.app.competition.repository;

import cent.wong.compedia.entity.CompetitionTracker;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompetitionTrackerRepository extends R2dbcRepository<CompetitionTracker, Long> {}
