package cent.wong.compedia.app.user.controller;

import cent.wong.compedia.app.user.service.UserService;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.user.*;
import cent.wong.compedia.security.authentication.JwtAuthentication;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "User")
public class UserController {

    private final UserService service;

    @Operation(description = "Save user")
    @PostMapping("/public/v1/user/save")
    public Mono<ResponseEntity<BaseResponse<SaveUserRes>>> save(
            @RequestBody @Valid SaveUserReq req
            ){
        return this.service.save(req)
                .map(ResponseEntity::ok);
    }

    @Operation(description = "Login user")
    @PostMapping("/public/v1/user/login")
    public Mono<ResponseEntity<BaseResponse<LoginUserRes>>> login(
            @RequestBody @Valid LoginUserReq req
            ){
        return this.service.login(req)
                .map(ResponseEntity::ok);
    }

    @Operation(description = "Get user")
    @PostMapping("/private/v1/user/get")
    public Mono<ResponseEntity<BaseResponse<GetUserRes>>> get(
            @RequestBody GetUserReq req
    ){
        return this.service.get(req)
                .map(ResponseEntity::ok);
    }

    @Operation(description = "Get list of user")
    @PostMapping("/private/v1/user/get/list")
    public Mono<ResponseEntity<BaseResponse<PaginationRes<GetUserRes>>>> getList(
            @RequestBody GetUserReq req
    ){
        return this.service.getList(req)
                .map(ResponseEntity::ok);
    }

    @Operation(description = "Update user")
    @PostMapping("/private/v1/user/update")
    public Mono<ResponseEntity<BaseResponse<?>>> update(
            Authentication authentication,
            @RequestBody @Valid UpdateUserReq req
    ){
        GetUserReq getReq = new GetUserReq();
        getReq.setId(Integer.valueOf((int)authentication.getPrincipal()).longValue());
        return this.service.update(getReq, req)
                .map(ResponseEntity::ok);
    }

    @Operation(description = "Activate deleted user")
    @PostMapping("/private/v1/user/activate")
    public Mono<ResponseEntity<BaseResponse<?>>> activate(
            Authentication authentication
    ){
        GetUserReq getReq = new GetUserReq();
        getReq.setId(Integer.valueOf((int)authentication.getPrincipal()).longValue());
        return this.service.activate(getReq)
                .map(ResponseEntity::ok);
    }

    @Operation(description = "Delete user")
    @PostMapping("/private/v1/user/delete")
    public Mono<ResponseEntity<BaseResponse<?>>> delete(
            JwtAuthentication authentication
    ){
        GetUserReq getReq = new GetUserReq();
        getReq.setId(Integer.valueOf((int)authentication.getPrincipal()).longValue());
        return this.service.delete(getReq)
                .map(ResponseEntity::ok);
    }
}
