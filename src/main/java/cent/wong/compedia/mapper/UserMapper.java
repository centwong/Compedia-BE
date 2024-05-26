package cent.wong.compedia.mapper;

import cent.wong.compedia.entity.User;
import cent.wong.compedia.entity.dto.user.GetUserRes;
import cent.wong.compedia.entity.dto.user.LoginUserReq;
import cent.wong.compedia.entity.dto.user.SaveUserReq;
import cent.wong.compedia.entity.dto.user.UpdateUserReq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User saveReq(SaveUserReq req);

    User loginReq(LoginUserReq req);

    GetUserRes getUserRes(User user);

    @Mapping(
            source = "deletedAt",
            target = "deletedAt",
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL
    )
    User updateUser(UpdateUserReq req, @MappingTarget User user);
}
