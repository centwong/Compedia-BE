package cent.wong.compedia.mapper;

import cent.wong.compedia.entity.Competition;
import cent.wong.compedia.entity.dto.competition.SaveUpdateCompetitionReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CompetitionMapper {

    CompetitionMapper INSTANCE = Mappers.getMapper(CompetitionMapper.class);

    @Mapping(
            source = "fkInterestTypeIds",
            target = "fkInterestTypeIds",
            ignore = true
    )
    Competition save(SaveUpdateCompetitionReq req);

    @Mapping(
            source = "fkInterestTypeIds",
            target = "fkInterestTypeIds",
            ignore = true
    )
    Competition update(SaveUpdateCompetitionReq req, @MappingTarget Competition competition);
}
