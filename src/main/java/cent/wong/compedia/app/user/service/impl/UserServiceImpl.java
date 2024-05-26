package cent.wong.compedia.app.user.service.impl;

import cent.wong.compedia.app.user.repository.UserRepository;
import cent.wong.compedia.app.user.service.UserService;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.constant.RoleConstant;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.User;
import cent.wong.compedia.entity.dto.pddikti.GetDetailStudent;
import cent.wong.compedia.entity.dto.pddikti.GetListStudentRes;
import cent.wong.compedia.entity.dto.user.*;
import cent.wong.compedia.mapper.UserMapper;
import cent.wong.json.JsonUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.function.Function;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final WebClient.Builder webclient;

    private final UserRepository userRepository;

    private final Tracer tracer;

    private final JsonUtil jsonUtil;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    @Value("${jwt.secret}")
    private String jwtSecret;

    // name must be full name
    private Mono<GetListStudentRes.StudentData> fetchListStudent(String name){
        String url = String.format("https://api-frontend.kemdikbud.go.id/hit_mhs/%s", URLEncoder.encode(name, StandardCharsets.UTF_8));
        log.info("fetching url list: {}", url);
        return webclient
                .baseUrl(url)
                .build()
                .get()
                .retrieve()
                .bodyToMono(GetListStudentRes.class)
                .map((getListStudentRes) -> {
                    return getListStudentRes
                            .getMahasiswa()
                            .stream()
                            .peek((data) -> {
                                String currentText = data.getText();
                                String currentWebsiteLink = data.getWebsiteLink();

                                String[] splitterText = currentText.split(", ");
                                String[] splitterWebsiteLink = currentWebsiteLink.split("/");

                                if(splitterText.length == 3){
                                    data.setText(currentText.split(", ")[0]);
                                }
                                if(splitterWebsiteLink.length == 3){
                                    data.setWebsiteLink(currentWebsiteLink.split("/")[2]);
                                }
                            })
                            .filter((data) -> {
                                String currentText = data.getText();
                                int index = currentText.indexOf('(');

                                // currentText represent the name of the student
                                if(index != -1){
                                    currentText = currentText.substring(0, index).toLowerCase();
                                }
                                return currentText.equalsIgnoreCase(name);
                            })
                            .toList();
                })
                .flatMap((data) -> {
                    if(data.isEmpty()){
                        return Mono.empty();
                    } else {
                        return Mono.just(data.get(0));
                    }
                })
                .doOnNext((d) -> log.info("success fetch user from pddikti: {}", jsonUtil.writeValueAsString(d)));
    }

    // you get id from fetchListStudent() method
    private Mono<GetDetailStudent> fetchStudentDetail(String id){
        String url = String.format("https://api-frontend.kemdikbud.go.id/detail_mhs/%s", id);
        log.info("fetching url detail: {}", url);
        return webclient
                .baseUrl(url)
                .build()
                .get()
                .retrieve()
                .bodyToMono(GetDetailStudent.class)
                .doOnNext((d) -> log.info("success fetch user detail from pddikti: {}", jsonUtil.writeValueAsString(d)));
    }

    @Override
    public Mono<BaseResponse<SaveUserRes>> save(SaveUserReq req) {
        User user = this.userMapper.saveReq(req);
        user.setPassword(bcrypt.encode(req.getPassword()));
        user.setRole(RoleConstant.USER.getId());
        int atIndex = req.getEmail().indexOf("@");
        user.setDomain(req.getEmail().substring(atIndex+1));

        GetUserReq getReq = new GetUserReq();
        getReq.setEmail(req.getEmail());
        Mono<User> findUserByEmail = this.userRepository.get(getReq);

        Mono<GetListStudentRes.StudentData> fetchStudentData = this.fetchListStudent(req.getName());

        Function<GetListStudentRes.StudentData, Mono<GetDetailStudent>> fetchStudentDetail = (d) -> {
              Mono<GetDetailStudent> fetchDetailStudent = this.fetchStudentDetail(d.getWebsiteLink());
              return fetchDetailStudent;
        };

        Mono<BaseResponse<SaveUserRes>> saveUser = this.userRepository.save(user)
                .map((data) -> {
                    SaveUserRes res = new SaveUserRes();
                    res.setId(data.getId());
                    return BaseResponse.sendSuccess(tracer, res);
                });

        return findUserByEmail
                .map((d) -> BaseResponse.<SaveUserRes>sendError(tracer, ErrorCode.EMAIL_ALREADY_EXIST.getErrCode(), ErrorCode.EMAIL_ALREADY_EXIST.getMessage()))
                .switchIfEmpty(
                        fetchStudentData
                                .flatMap(fetchStudentDetail)
                                .flatMap((data) -> {
                                    GetDetailStudent.CommonData commonData = data.getDataumum();
                                    if(commonData.getDateGraduate() != null || commonData.getDiploma() != null || commonData.getStatus() != null){
                                        return Mono.just(BaseResponse.<SaveUserRes>sendError(tracer, ErrorCode.ALREADY_GRADUATE_STUDENT_ERROR.getErrCode(), ErrorCode.ALREADY_GRADUATE_STUDENT_ERROR.getMessage()));
                                    } else {
                                        return saveUser;
                                    }
                                })
                                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.STUDENT_NOT_FOUND_PDDIKTI.getErrCode(), ErrorCode.STUDENT_NOT_FOUND_PDDIKTI.getMessage())))
                )
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<LoginUserRes>> login(LoginUserReq req) {

        GetUserReq getReq = new GetUserReq();
        getReq.setEmail(req.getEmail());
        Mono<User> fetchUser = this.userRepository
                .get(getReq);

        return fetchUser
                .map((data) -> {
                    if(bcrypt.matches(req.getPassword(), data.getPassword())){
                        String jwtToken = Jwts
                                .builder()
                                .signWith(SignatureAlgorithm.HS256, Decoders.BASE64.decode(jwtSecret))
                                .claim("id", data.getId())
                                .claim("role", data.getRole())
                                .compact();

                        LoginUserRes res  = new LoginUserRes();
                        res.setId(data.getId());
                        res.setName(data.getName());
                        res.setJwtToken(jwtToken);

                        return BaseResponse.sendSuccess(tracer, res, "User authenticated");
                    } else {
                        return BaseResponse.<LoginUserRes>sendError(tracer, ErrorCode.INVALID_CREDENTIAL.getErrCode(), ErrorCode.INVALID_CREDENTIAL.getMessage());
                    }
                })
                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.STUDENT_NOT_FOUND_PDDIKTI.getErrCode(), ErrorCode.STUDENT_NOT_FOUND.getMessage())))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<GetUserRes>> get(GetUserReq req) {
        req.setIsActive(true);
        return this.userRepository.get(req)
                .map((data) -> userMapper.getUserRes(data))
                .map((data) -> BaseResponse.sendSuccess(tracer, data))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<PaginationRes<GetUserRes>>> getList(GetUserReq req) {
        req.setIsActive(true);
        return this.userRepository.getList(req)
                .map((data) -> {
                    PaginationRes.PaginationResBuilder<GetUserRes> res = PaginationRes.<GetUserRes>builder().pg(data.getPg());

                    res.list(
                            data.getList()
                                    .stream()
                                    .map((user) -> userMapper.getUserRes(user))
                                    .toList()
                    );

                    return BaseResponse.sendSuccess(tracer, res.build());
                })
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<Object>> update(GetUserReq req, UpdateUserReq updateReq) {

        Long now = new Date().getTime();

        req.setIsActive(true);

        Mono<User> findUser = this.userRepository.get(req);

        Function<User, Mono<BaseResponse<Object>>> processUpdate = (user) -> {
            User updateUser = this.userMapper.updateUser(updateReq, user);
            updateUser.setUpdatedAt(now);
            return this.userRepository.update(updateUser)
                    .map((d) -> BaseResponse.sendSuccess(tracer));
        };

        return findUser
                .flatMap(processUpdate)
                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.STUDENT_NOT_FOUND_PDDIKTI.getErrCode(), ErrorCode.STUDENT_NOT_FOUND.getMessage())));
    }

    @Override
    public Mono<BaseResponse<Object>> activate(GetUserReq req) {
        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setIsActive(true);
        updateUserReq.setDeletedAt(null);
        return this.update(req, updateUserReq);
    }

    @Override
    public Mono<BaseResponse<Object>> delete(GetUserReq req) {
        UpdateUserReq updateUserReq = new UpdateUserReq();
        updateUserReq.setIsActive(false);
        updateUserReq.setDeletedAt(new Date().getTime());
        return this.update(req, updateUserReq);
    }
}
