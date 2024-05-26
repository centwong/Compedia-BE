package cent.wong.compedia.app.user.service;

import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.user.*;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<BaseResponse<SaveUserRes>> save(SaveUserReq req);
    Mono<BaseResponse<LoginUserRes>> login(LoginUserReq req);
    Mono<BaseResponse<GetUserRes>> get(GetUserReq req);
    Mono<BaseResponse<PaginationRes<GetUserRes>>> getList(GetUserReq req);
    Mono<BaseResponse<Object>> update(GetUserReq req, UpdateUserReq updateReq);
    Mono<BaseResponse<Object>> activate(GetUserReq req);
    Mono<BaseResponse<Object>> delete(GetUserReq req);
}
