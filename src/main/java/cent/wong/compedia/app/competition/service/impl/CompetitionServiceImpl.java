package cent.wong.compedia.app.competition.service.impl;

import cent.wong.compedia.app.competition.repository.CompetitionRepository;
import cent.wong.compedia.app.competition.repository.CompetitionTrackerRepository;
import cent.wong.compedia.app.competition.service.CompetitionService;
import cent.wong.compedia.app.interest.repository.CompetitionInterestTypeRepository;
import cent.wong.compedia.app.interest.repository.InterestRepository;
import cent.wong.compedia.app.university.repository.UniversityRepository;
import cent.wong.compedia.constant.ColorConstant;
import cent.wong.compedia.constant.CompetitionPaidStatus;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.*;
import cent.wong.compedia.entity.dto.competition.*;
import cent.wong.compedia.entity.dto.university.GetUniversityReq;
import cent.wong.compedia.mapper.CompetitionMapper;
import cent.wong.compedia.util.AuthenticationUtil;
import cent.wong.compedia.util.CloudinaryUtil;
import cent.wong.compedia.util.DateUtil;
import cent.wong.compedia.util.MidtransUtil;
import cent.wong.entity.Pagination;
import cent.wong.json.JsonUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@Slf4j
@RequiredArgsConstructor
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;

    private final CompetitionMapper competitionMapper = CompetitionMapper.INSTANCE;

    private final AuthenticationUtil authenticationUtil;

    private final CloudinaryUtil cloudinaryUtil;

    private final Tracer tracer;

    private final Cloudinary cloudinary;

    private final JsonUtil jsonUtil;

    private final DateUtil dateUtil;

    private final UniversityRepository universityRepository;

    private final InterestRepository interestRepository;

    private final CompetitionInterestTypeRepository competitionInterestRepository;

    private final CompetitionTrackerRepository competitionTrackerRepository;

    @Override
    public Mono<BaseResponse<SaveUpdateCompetitionRes>> save(Authentication authentication, SaveUpdateCompetitionReq req, Mono<FilePart> file) {
        Competition competition = competitionMapper.save(req);
        competition.setCreatedAt(Instant.now().toEpochMilli());
        competition.setFkInterestTypeIds(jsonUtil.writeValueAsString(req.getFkInterestTypeIds()));
        competition.setFkInterestTimeId(req.getFkInterestTimeId());
        competition.setIsActive(true);

        if(req.getIsRegistrationPaid()){
            competition.setCompetitionPaidStatus(CompetitionPaidStatus.NOT_PAID.getStatus());
        } else {
            competition.setCompetitionPaidStatus(CompetitionPaidStatus.FREE.getStatus());
        }

        // file is supposed to be not null!
        Mono<CloudinaryResponse> uploadImage = file.flatMap((f) ->
                f.content()
                   .flatMap((dataBuffer) -> {
                       try {
                           return Mono.just(cloudinaryUtil.extractMap(cloudinary.uploader().upload(cloudinaryUtil.readBytes(dataBuffer), ObjectUtils.asMap("folder", "competition"))));
                       } catch (IOException e) {
                           return Mono.error(e);
                       }
                   })
                .collectList()
                .map((data) -> data.get(0))
        );

        return uploadImage
                .flatMap(
                        (cloudinaryResponse) -> {
                            competition.setImage(cloudinaryResponse.getSecureUrl());
                            competition.setImageId(cloudinaryResponse.getPublicId());
                            return this.competitionRepository.save(competition)
                                    .zipWhen((savedCompetition) ->
                                            Flux.fromIterable(req.getFkInterestTypeIds())
                                                    .flatMap((interestId) -> {
                                                        CompetitionInterestType competitionInterestType = new CompetitionInterestType();
                                                        competitionInterestType.setFkCompetitionId(competition.getId());
                                                        competitionInterestType.setFkInterestTypeId(interestId);
                                                        return this.competitionInterestRepository.save(competitionInterestType);
                                                    })
                                                    .collectList()
                                    )
                                    .map(Tuple2::getT1)
                                    .flatMap((competition1) -> saveCompetitionTracker(competition.getId()))
                                    .flatMap((competitionTracker) -> {
                                        SaveUpdateCompetitionRes res = new SaveUpdateCompetitionRes();
                                        res.setUniqueId(competitionTracker.getId());
                                        res.setPaymentId(competitionTracker.getUniqueId());

                                        if(req.getIsRegistrationPaid()){
                                            return createPayment(competition, competitionTracker, req.getPaymentAmount())
                                                    .map((paymentResult) -> {
                                                        res.setPaymentUri(paymentResult.getRedirectUrl());
                                                        return res;
                                                    });
                                        } else {
                                            return Mono.just(res);
                                        }
                                    })
                                    .map((data) -> BaseResponse.<SaveUpdateCompetitionRes>sendSuccess(tracer, data));
                        }
                )
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    private Mono<CompetitionTracker> saveCompetitionTracker(Long competitionId){
        CompetitionTracker competitionTracker = new CompetitionTracker();
        competitionTracker.setCompetitionId(competitionId);
        competitionTracker.setUniqueId(UUID.randomUUID().toString());
        return competitionTrackerRepository.save(competitionTracker);
    }

    private Mono<MidtransSnapURI> createPayment(Competition competition, CompetitionTracker competitionTracker, Long totalPayment){
        String transactionToken = null;
        try {
            transactionToken = SnapApi.createTransactionToken(
                    MidtransUtil.generateSnapData(
                            TransactionDetails
                                    .builder()
                                    .grossAmount(totalPayment)
                                    .orderId(competitionTracker.getUniqueId())
                                    .build(),
                            CustomerDetails
                                    .builder()
                                    .email(competition.getPublisherEmail())
                                    .firstName(competition.getPublisherName())
                                    .build()
                    ).convertIntoMap()
            );
            MidtransSnapURI uri = new MidtransSnapURI();
            uri.setRedirectUrl(String.format("https://app.sandbox.midtrans.com/snap/v2/vtweb/%s", transactionToken));
            return Mono.just(uri);
        } catch (MidtransError e) {
            return Mono.error(e);
        }
    }

    @Override
    public Mono<BaseResponse<GetCompetitionDetailRes>> get(GetCompetitionReq req) {
        req.getPgParam().setLimit(1);
        GetCompetitionInterestTypeReq getCompetitionInterestTypeReq = new GetCompetitionInterestTypeReq();
        getCompetitionInterestTypeReq.setFkCompetitionId(req.getId());
        getCompetitionInterestTypeReq.setIds(req.getIds());
        getCompetitionInterestTypeReq.setFkInterestTypeId(req.getFkInterestTypeId());
        getCompetitionInterestTypeReq.setFkInterestTypeIds(req.getFkInterestTypeIds());

        Mono<List<CompetitionInterestType>> competitionInterestTypeList = this.competitionInterestRepository
                        .getList(getCompetitionInterestTypeReq);

        req.setIsActive(true);

        return competitionInterestTypeList.map((competitionInterestType) -> competitionInterestType.stream().map(CompetitionInterestType::getFkCompetitionId).toList())
                .flatMap((listFkCompetitionId) -> {
                    req.setIds(listFkCompetitionId);
                    return this.competitionRepository.get(req);
                })
                .map((data) -> {
                    data.setFkInterestTypeId(jsonUtil.readValue(data.getFkInterestTypeIds(), new TypeReference<List<Long>>() {}));
                    return data;
                })
                .flatMap((competition) -> {
                    // find interest time first
                    Mono<List<InterestTime>> time = this.interestRepository.getAllInterestTime();

                    // find interest type too
                    Mono<List<InterestType>> type = this.interestRepository.getAllInterestType();

                    Mono<University> getUniversity = this.universityRepository.get(
                            GetUniversityReq
                                    .builder()
                                    .id(1L) // TODO: change this when the UI is exist!
                                    .pgParam(new Pagination.PaginationParam())
                                    .build()
                    );

                    return Mono.zip(
                                time,
                                type,
                                getUniversity
                            )
                            .map((t2) -> {
                                List<InterestTime> interestTimes = t2.getT1();
                                List<InterestType> interestTypes = t2.getT2();
                                University university = t2.getT3();
                                return convertToDetailFromCompetition(
                                        competition,
                                        university,
                                        interestTypes,
                                        interestTimes
                                );
                            });
                })
                .map((data) -> BaseResponse.sendSuccess(tracer, data))
                .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    private GetCompetitionDetailRes convertToDetailFromCompetition(
            Competition competition,
            University university,
            List<InterestType> interestTypes,
            List<InterestTime> interestTimes
    ){
        GetCompetitionDetailRes detail = new GetCompetitionDetailRes();
        detail.setId(competition.getId());
        detail.setName(competition.getName());
        detail.setImage(competition.getImage());
        detail.setDeadline(dateUtil.convertIntoString(competition.getDeadline()));
        detail.setPrice(competition.getPrice());
        detail.setUniversityName(university.getName());
        detail.setPublishedBy(competition.getPublisherName());

        interestTimes.forEach((times) -> {
            if(times.getId() == competition.getFkInterestTimeId()){
                detail.setLocation(times.getTime() + "-" + competition.getCityName());
            }
        });

        List<String> resType = new ArrayList<>();

        competition.getFkInterestTypeId()
                .forEach((types) -> {
                    interestTypes.forEach((interestType) -> {
                        if(types == interestType.getId()){
                            resType.add(interestType.getType());
                        }
                    });
                });

        Instant deadline = Instant.ofEpochMilli(competition.getDeadline());
        Instant now = Instant.now();

        if(deadline.isBefore(now)){
            detail.setIsClosed(true);
        } else {
            detail.setIsClosed(false);
        }

        detail.setType(resType);
        detail.setDescription(competition.getDescription());
        detail.setWinnerPrize(competition.getPrizePool());
        detail.setGuidebookLink(competition.getLinkGuidebook());
        detail.setCompetitionFee(competition.getCompetitionFee());
        detail.setLinkCompetitionRegistration(competition.getLinkCompetitionRegistration());
        return detail;
    }

    // TODO: the logic is not efficient, change the algorithm later!
    private GetCompetitionRes convertFromCompetition(
            Competition competition,
            List<InterestType> interestTypes,
            List<InterestTime> interestTimes
    ){
        GetCompetitionRes res = new GetCompetitionRes();
        res.setId(competition.getId());
        res.setImage(competition.getImage());
        res.setName(competition.getName());
        res.setDeadline(dateUtil.convertIntoString(competition.getDeadline()));
        res.setPrice(competition.getPrice());

        interestTimes.forEach((times) -> {
            if(times.getId() == competition.getFkInterestTimeId()){
                res.setLocation(times.getTime() + "-" + competition.getCityName());
            }
        });

        List<String> resType = new ArrayList<>();

        competition.getFkInterestTypeId()
                .forEach((types) -> {
                    interestTypes.forEach((interestType) -> {
                        if(types == interestType.getId()){
                            resType.add(interestType.getType());
                        }
                    });
                });

        res.setType(resType);

        Instant deadline = Instant.ofEpochMilli(competition.getDeadline());
        Instant now = Instant.now();

        if(deadline.isBefore(now)){
            res.setDaySinceDeadline("Sudah Tutup");
            res.setDaySinceDeadlineColor(ColorConstant.RED);
            res.setIsClosed(true);
        } else {
            Long differenceDay = ChronoUnit.DAYS.between(deadline, now) / -1;
            res.setDaySinceDeadline(String.valueOf(differenceDay));
            res.setIsClosed(false);

            if(differenceDay > 7){
                res.setDaySinceDeadlineColor(ColorConstant.RED);
            } else {
                res.setDaySinceDeadlineColor(ColorConstant.GREEN);
            }
        }

        return res;
    }

    @Override
    public Mono<BaseResponse<PaginationRes<GetCompetitionRes>>> getList(GetCompetitionReq req) {

        GetCompetitionInterestTypeReq getCompetitionInterestTypeReq = new GetCompetitionInterestTypeReq();
        getCompetitionInterestTypeReq.setFkCompetitionId(req.getId());
        getCompetitionInterestTypeReq.setIds(req.getIds());
        getCompetitionInterestTypeReq.setFkInterestTypeId(req.getFkInterestTypeId());
        getCompetitionInterestTypeReq.setFkInterestTypeIds(req.getFkInterestTypeIds());

        Mono<List<CompetitionInterestType>> competitionInterestTypeList = this.competitionInterestRepository
                .getList(getCompetitionInterestTypeReq);

        req.setIsActive(true);
        return competitionInterestTypeList.map((competitionInterestType) -> competitionInterestType.stream().map(CompetitionInterestType::getFkCompetitionId).toList())
                .flatMap((listFkCompetitionId) -> {
                    req.setIds(listFkCompetitionId);
                    req.setCompetitionPaidStatuses(List.of(
                            CompetitionPaidStatus.FREE.getStatus(),
                            CompetitionPaidStatus.PAID.getStatus()
                    ));
                    return this.competitionRepository.getList(req);
                })
                .map((data) -> {
                    data.getList().forEach((competition) -> {
                        competition.setFkInterestTypeId(jsonUtil.readValue(competition.getFkInterestTypeIds(), new TypeReference<List<Long>>() {}));
                    });
                    return data;
                })
                .flatMap((data) -> {
                    // find interest time first
                    Mono<List<InterestTime>> time = this.interestRepository.getAllInterestTime();

                    // find interest type too
                    Mono<List<InterestType>> type = this.interestRepository.getAllInterestType();

                    return time.zipWith(type)
                            .map((t2) -> {
                                List<InterestTime> interestTimes = t2.getT1();
                                List<InterestType> interestTypes = t2.getT2();
                                List<Competition> listCompetition = data.getList();

                                List<GetCompetitionRes> getRes = listCompetition
                                        .stream()
                                        .map((competition) -> convertFromCompetition(competition, interestTypes, interestTimes))
                                        .collect(Collectors.toList());

                                return PaginationRes
                                        .<GetCompetitionRes>builder()
                                        .pg(data.getPg())
                                        .list(getRes)
                                        .build();
                            });
                })
                .map((data) -> BaseResponse.sendSuccess(tracer, data))
                .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<Object>> update(Authentication authentication, GetCompetitionReq getCompetitionReq, SaveUpdateCompetitionReq req, Mono<FilePart> file) {

        Mono<Competition> findCompetition = this.competitionRepository.get(getCompetitionReq);

        Mono<CloudinaryResponse> uploadImage = file.flatMap((f) ->
                f.content()
                        .flatMap((dataBuffer) -> {
                            try {
                                return Mono.just(cloudinaryUtil.extractMap(cloudinary.uploader().upload(cloudinaryUtil.readBytes(dataBuffer), ObjectUtils.asMap("folder", "competition"))));
                            } catch (IOException e) {
                                return Mono.error(e);
                            }
                        })
                        .collectList()
                        .map((data) -> data.get(0))
        );

        Function<Competition, Object> deleteOldImage = (competition) -> {
            try {
                return Mono.just(this.cloudinary.uploader().destroy(competition.getImageId(), ObjectUtils.emptyMap()));
            } catch (IOException e) {
                return Mono.error(e);
            }
        };

        return findCompetition
                .map((competition) -> this.competitionMapper.update(req, competition))
                .flatMap((updatedCompetition) -> {
                    try {
                        if(req.getFkInterestTypeIds() != null){
                            updatedCompetition.setFkInterestTypeIds(jsonUtil.writeValueAsString(req.getFkInterestTypeIds()));
                        }
                        updatedCompetition.setUpdatedAt(Instant.now().toEpochMilli());
                        updatedCompetition.setUpdatedBy(authenticationUtil.extractId(authentication));

                        this.cloudinary.uploader().destroy(updatedCompetition.getImageId(), ObjectUtils.emptyMap());
                        return uploadImage
                                .map((resp) -> {
                                    updatedCompetition.setImage(resp.getSecureUrl());
                                    updatedCompetition.setImageId(resp.getPublicId());
                                    return updatedCompetition;
                                })
                                .switchIfEmpty(Mono.just(updatedCompetition))
                                .flatMap((data) -> this.competitionRepository.update(data));
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .map((res) -> BaseResponse.sendSuccess(tracer))
                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.COMPETITION_NOT_FOUND.getErrCode(), ErrorCode.COMPETITION_NOT_FOUND.getMessage())))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<Object>> delete(Authentication authentication, GetCompetitionReq req) {
        return this.competitionRepository.get(req)
                .flatMap((data) -> {
                    data.setIsActive(false);
                    data.setDeletedAt(Instant.now().toEpochMilli());
                    data.setDeletedBy(authenticationUtil.extractId(authentication));
                    return this.competitionRepository.update(data);
                })
                .map((res) -> BaseResponse.sendSuccess(tracer))
                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.COMPETITION_NOT_FOUND.getErrCode(), ErrorCode.COMPETITION_NOT_FOUND.getMessage())))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<Object>> activate(Authentication authentication, GetCompetitionReq req) {
        return this.competitionRepository.get(req)
                .flatMap((data) -> {
                    data.setIsActive(true);
                    data.setDeletedAt(null);
                    data.setDeletedBy(null);
                    return this.competitionRepository.update(data);
                })
                .map((res) -> BaseResponse.sendSuccess(tracer))
                .switchIfEmpty(Mono.just(BaseResponse.sendError(tracer, ErrorCode.COMPETITION_NOT_FOUND.getErrCode(), ErrorCode.COMPETITION_NOT_FOUND.getMessage())))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }
}
