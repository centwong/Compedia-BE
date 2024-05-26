package cent.wong.compedia.app.user.repository;

import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.User;
import cent.wong.compedia.entity.dto.user.GetUserReq;
import reactor.core.publisher.Mono;

public interface UserRepository {
    Mono<User> save(User user);
    Mono<User> get(GetUserReq req);
    Mono<PaginationRes<User>> getList(GetUserReq req);
    Mono<?> update(User user);
}
