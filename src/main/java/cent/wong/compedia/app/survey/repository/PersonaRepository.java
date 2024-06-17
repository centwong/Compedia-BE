package cent.wong.compedia.app.survey.repository;

import cent.wong.compedia.entity.Persona;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface PersonaRepository extends R2dbcRepository<Persona, Long> {}
