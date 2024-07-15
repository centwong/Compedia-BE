package cent.wong.compedia.app.user.service.impl;

import cent.wong.compedia.app.interest.repository.InterestRepository;
import cent.wong.compedia.app.user.repository.MentorDataRepository;
import cent.wong.compedia.app.user.repository.MentorInterestTypeRepository;
import cent.wong.compedia.app.user.repository.UserRepository;
import cent.wong.compedia.app.user.service.MentorService;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.constant.ApprovalStatus;
import cent.wong.compedia.constant.RoleConstant;
import cent.wong.compedia.entity.*;
import cent.wong.compedia.entity.dto.interest.GetInterestReq;
import cent.wong.compedia.entity.dto.mentor.GetMentorDataReq;
import cent.wong.compedia.entity.dto.mentor.GetMentorDetailRes;
import cent.wong.compedia.entity.dto.mentor.GetMentorReq;
import cent.wong.compedia.entity.dto.mentor.GetMentorRes;
import cent.wong.compedia.entity.dto.user.GetUserReq;
import cent.wong.compedia.entity.dto.user.MentorUpdateApprovalReq;
import cent.wong.compedia.entity.dto.user.MentorUpdateDetailReq;
import cent.wong.compedia.entity.dto.user.SaveMentorDataReq;
import cent.wong.compedia.mapper.UserMapper;
import cent.wong.compedia.util.AuthenticationUtil;
import cent.wong.entity.Pagination;
import cent.wong.json.JsonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
@Slf4j
public class MentorServiceImpl implements MentorService {

    private final MentorDataRepository mentorDataRepository;

    private final MentorInterestTypeRepository mentorInterestTypeRepository;

    private final JsonUtil jsonUtil;

    private final AuthenticationUtil authenticationUtil;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private final Tracer tracer;

    private final UserRepository userRepository;
    
    private final InterestRepository interestRepository;

    @CacheEvict(value = {
            "mentor:data:get",
            "mentor:detail",
            "mentor:list"
    }, allEntries = true)
    @Override
    public Mono<BaseResponse<MentorData>> save(Authentication authentication, SaveMentorDataReq req) {
        log.info("accepting mentor data to be inserted: {}", jsonUtil.writeValueAsString(req));

        Long userId = this.authenticationUtil.extractId(authentication);

        MentorData mentorData = this.userMapper.saveMentorData(req);
        mentorData.setCreatedAt(Instant.now().toEpochMilli());
        mentorData.setCreatedBy(userId);
        mentorData.setApprovalStatus(ApprovalStatus.WAITING_FOR_APPROVAL.getStatus());
        mentorData.setFkUserId(userId);

        // save the mentor interest type too
        Mono<List<MentorInterestType>> saveMentorInterestType = this.mentorInterestTypeRepository
                .saveAll(
                        req.getFkInterestTypeIds()
                                .stream()
                                .map(typeId -> {
                                    MentorInterestType mentorInterestType = new MentorInterestType();
                                    mentorInterestType.setFkInterestTypeId(typeId);
                                    mentorInterestType.setFkUserId(userId);
                                    return mentorInterestType;
                                })
                                .toList()
                )
                .collectList();

        Mono<MentorData> saveMentorData = this.mentorDataRepository
                .save(mentorData);

        return saveMentorData.zipWith(saveMentorInterestType)
                .map(Tuple2::getT1)
                .map((mentorData1) -> BaseResponse.sendSuccess(tracer, mentorData1))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Cacheable("mentor:data:get")
    @Override
    public Mono<BaseResponse<GetMentorDataReq>> get(Authentication authentication) {
        Long userId = authenticationUtil.extractId(authentication);
        log.info("accepting get mentor data from userId: {}", userId);

        MentorData exampleData = new MentorData();
        exampleData.setFkUserId(userId);

        return this.mentorDataRepository.findOne(
                Example.of(exampleData)
        )
                .map((mentorData) -> {
                    GetMentorDataReq getMentorDataReq = new GetMentorDataReq();
                    ApprovalStatus approvalStatus = ApprovalStatus.getApprovalStatus(mentorData.getApprovalStatus());
                    getMentorDataReq.setId(mentorData.getId());
                    if(approvalStatus == ApprovalStatus.ACCEPTED){
                        getMentorDataReq.setIsAccepted(true);
                    } else if(approvalStatus == ApprovalStatus.WAITING_FOR_APPROVAL){
                        getMentorDataReq.setIsWaiting(true);
                    } else if(approvalStatus == ApprovalStatus.REJECTED){
                        getMentorDataReq.setIsRejected(true);
                    }

                    return getMentorDataReq;
                })
                .map((mentorData) -> BaseResponse.sendSuccess(tracer, mentorData))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Cacheable(value = "mentor:detail")
    @Override
    public Mono<BaseResponse<GetMentorDetailRes>> getMentorDetail(GetUserReq getReq) {
        log.info("accepting getMentorDetail req: {}", jsonUtil.writeValueAsString(getReq));

        getReq.getPgParam().setLimit(1);
        getReq.setRole(RoleConstant.MENTOR.getId());
        Mono<User> getUser = this.userRepository.get(
                getReq
        );

        GetMentorDetailRes getMentorDetailRes = new GetMentorDetailRes();

        return getUser.flatMap((user) -> {
            getMentorDetailRes.setId(user.getId());
            getMentorDetailRes.setName(user.getName());
            return this.mentorDataRepository.findByFkUserId(user.getId());
        })
        .map((mentorData) -> {
            getMentorDetailRes.setMentoringScope(mentorData.getMentoringScope());
            getMentorDetailRes.setRole(mentorData.getJobTitle());
            getMentorDetailRes.setNotes(mentorData.getTncDescription());
            getMentorDetailRes.setAchievement(mentorData.getAchievement());
            getMentorDetailRes.setDescription(mentorData.getDescription());
            getMentorDetailRes.setInstagramLink(mentorData.getInstagramUrl());
            getMentorDetailRes.setLinkedinLink(mentorData.getLinkedinUrl());
            return mentorData;
        })
        .flatMap((mentorData) -> this.mentorInterestTypeRepository.findByFkUserId(mentorData.getFkUserId()).collectList())
        .zipWith(this.interestRepository.getAllInterestType())
        .map((t2) -> {
            List<Long> interestIds = t2.getT1()
                    .stream()
                    .map(MentorInterestType::getFkInterestTypeId)
                    .toList();
            getMentorDetailRes.setTypes(
                    getTypes(
                            interestIds,
                            t2.getT2()
                    )
            );
            return getMentorDetailRes;
        })
        .map((data) -> BaseResponse.sendSuccess(tracer, data))
        .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
        .onErrorResume((e) -> {
            log.error("error occurred with message: {}", e);
            return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
        });
    }

    @Cacheable("mentor:list")
    @Override
    public Mono<BaseResponse<PaginationRes<GetMentorRes>>> getList(Authentication authentication) {
        GetMentorReq getReq = new GetMentorReq();
        getReq.setId(authenticationUtil.extractId(authentication));
        log.info("accepting getList mentor req: {}", jsonUtil.writeValueAsString(getReq));

        Mono<PaginationRes<User>> getListUser = this.userRepository
                .getList(
                        GetUserReq
                                .builder()
                                .role(RoleConstant.MENTOR.getId())
                                .isActive(true)
                                .pgParam(new Pagination.PaginationParam())
                                .build()
                )
                .zipWith(
                        this.interestRepository.get(
                            GetInterestReq
                                    .builder()
                                    .fkUserId(authenticationUtil.extractId(authentication))
                                    .build())
                )
                .flatMap((t2) -> {
                    List<Long> interestTypesUser = jsonUtil.readValue(
                            t2.getT2().getFkInterestTypeIds(), new TypeReference<>() {}
                    );
                    List<User> users = t2.getT1().getList();

                    List<User> filteredMentor = new ArrayList<>();
                    var res = Flux.fromIterable(users)
                            .flatMap((user) -> {
                                return this.interestRepository.get(
                                        GetInterestReq
                                                .builder()
                                                .fkUserId(user.getId())
                                                .build()
                                )
                                .map((interestMentor) -> {
                                    return jsonUtil.readValue(
                                            interestMentor.getFkInterestTypeIds(),
                                            new TypeReference<List<Long>>() {}
                                    );
                                })
                                .map((interestMentorIds) -> {
                                    for(Long idInterestMentor: interestMentorIds){
                                        for(Long idInterestUser: interestTypesUser){
                                            // if the interest is same, then show it as recommendation
                                            if(idInterestMentor == idInterestUser){
                                                filteredMentor.add(user);
                                            }
                                        }
                                    }

                                    return interestMentorIds;
                                });
                            });

                    // change the detailed data in list mentor
                    t2.getT1().setList(filteredMentor);

                    return res.then(Mono.just(t2.getT1()));
                });

        return getListUser
                .flatMap((pgUser) -> {
                    List<User> users = pgUser.getList();
                    return Flux.fromIterable(users)
                            .flatMap((user) ->
                                 this.mentorDataRepository.findByFkUserId(user.getId())
                                         .map((mentorData) -> {
                                             GetMentorRes getMentorRes = new GetMentorRes();
                                             getMentorRes.setName(user.getName());
                                             getMentorRes.setAchievement(mentorData.getAchievement());
                                             getMentorRes.setUserId(user.getId());
                                             return getMentorRes;
                                         })
                                         .flatMap((mentorData) -> {
                                             return this.mentorInterestTypeRepository.findByFkUserId(mentorData.getUserId())
                                                     .collectList()
                                                     .zipWith(interestRepository.getAllInterestType())
                                                     .map((t2) -> {
                                                         List<Long> interestIds = t2.getT1()
                                                                 .stream()
                                                                 .map(MentorInterestType::getId)
                                                                 .toList();
                                                         List<InterestType> interestTypes = t2.getT2();
                                                         List<String> types = getTypes(interestIds, interestTypes);

                                                         mentorData.setType(types);
                                                         return mentorData;
                                                     });
                                         })
                            )
                            .collectList()
                            .map((listResponse) -> PaginationRes.<GetMentorRes>builder().list(listResponse).pg(pgUser.getPg()).build());
                })
                .map((pgRes) -> BaseResponse.sendSuccess(tracer, pgRes))
                .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    private static @NotNull List<String> getTypes(List<Long> interestIds, List<InterestType> interestTypes) {
        List<String> types = new ArrayList<>();
        for(int i = 0; i < interestIds.size() ; i++){
            Long currentId = interestIds.get(i);
            for(int j = 0; j < interestTypes.size() ; j++){
                InterestType currentInterestType = interestTypes.get(j);
                if(currentId == currentInterestType.getId()){
                    types.add(currentInterestType.getType());
                    break;
                }
            }
        }
        return types;
    }

    @CacheEvict(value = {
            "mentor:data:get",
            "mentor:detail",
            "mentor:list"
    }, allEntries = true)
    @Override
    public Mono<BaseResponse<Object>> updateApproval(Authentication authentication, MentorUpdateApprovalReq req) {
        Long adminId = authenticationUtil.extractId(authentication);
        log.info("accepting update mentor data from adminId: {} with approval: {}", adminId, jsonUtil.writeValueAsString(req));

        MentorData exampleData = new MentorData();
        exampleData.setId(req.getMentorDataId());
        Example<MentorData> exampleMentorData = Example.of(exampleData);

        return this.mentorDataRepository.findOne(exampleMentorData)
                .flatMap((mentorData) -> {
                    mentorData.setApprovalStatus(req.getStatus().getStatus());
                    mentorData.setUpdatedAt(Instant.now().toEpochMilli());
                    mentorData.setUpdatedBy(adminId);
                    Mono<MentorData> saveMentorData = this.mentorDataRepository.save(mentorData);
                    Mono<User> getAndUpdateUser = this.userRepository.get(
                            GetUserReq
                                    .builder()
                                    .id(mentorData.getFkUserId())
                                    .pgParam(new Pagination.PaginationParam())
                                    .build()
                    )
                            .flatMap((user) -> {
                                if(req.getStatus() == ApprovalStatus.ACCEPTED){
                                    user.setRole(RoleConstant.MENTOR.getId());
                                    user.setUpdatedAt(Instant.now().toEpochMilli());
                                    return this.userRepository.update(user)
                                            .thenReturn(user);
                                } else {
                                    return Mono.just(user);
                                }
                            });
                    return saveMentorData.zipWith(getAndUpdateUser)
                            .map(Tuple2::getT1);
                })
                .map((mentorData) -> BaseResponse.sendSuccess(tracer))
                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.MENTOR_DATA_NOT_FOUND.getErrCode(), ErrorCode.MENTOR_DATA_NOT_FOUND.getMessage())))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @CacheEvict(value = {
            "mentor:data:get",
            "mentor:detail",
            "mentor:list"
    }, allEntries = true)
    @Override
    public Mono<BaseResponse<Object>> updateMentorDetail(Authentication authentication, MentorUpdateDetailReq req) {
        MentorData exampleBody = new MentorData();
        exampleBody.setFkUserId(authenticationUtil.extractId(authentication));
        return this.mentorDataRepository
                .findOne(Example.of(exampleBody))
                .map((mentorData) -> {
                    MentorData updatedData = this.userMapper.updateMentorDataDetail(
                            req, mentorData
                    );
                    return this.mentorDataRepository.save(updatedData);
                })
                .map((mentorData) -> BaseResponse.sendSuccess(tracer))
                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.MENTOR_DATA_NOT_FOUND.getErrCode(), ErrorCode.MENTOR_DATA_NOT_FOUND.getMessage())))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }
}
