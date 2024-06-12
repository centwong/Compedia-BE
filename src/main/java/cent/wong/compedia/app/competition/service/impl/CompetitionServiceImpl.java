package cent.wong.compedia.app.competition.service.impl;

import cent.wong.compedia.app.competition.repository.CompetitionRepository;
import cent.wong.compedia.app.competition.service.CompetitionService;
import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.CloudinaryResponse;
import cent.wong.compedia.entity.Competition;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.competition.GetCompetitionReq;
import cent.wong.compedia.entity.dto.competition.SaveUpdateCompetitionReq;
import cent.wong.compedia.entity.dto.competition.SaveUpdateCompetitionRes;
import cent.wong.compedia.mapper.CompetitionMapper;
import cent.wong.compedia.util.AuthenticationUtil;
import cent.wong.compedia.util.CloudinaryUtil;
import cent.wong.json.JsonUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.function.Function;

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

    @Override
    public Mono<BaseResponse<SaveUpdateCompetitionRes>> save(Authentication authentication, SaveUpdateCompetitionReq req, Mono<FilePart> file) {
        Competition competition = competitionMapper.save(req);
        competition.setCreatedAt(Instant.now().toEpochMilli());
        competition.setCreatedBy(authenticationUtil.extractId(authentication));
        competition.setFkInterestTypeIds(jsonUtil.writeValueAsString(req.getFkInterestTypeIds()));
        competition.setFkUserId(authenticationUtil.extractId(authentication));
        competition.setFkInterestTimeId(req.getFkInterestTimeId());
        competition.setIsActive(true);

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
                                    .map((data) -> BaseResponse.<SaveUpdateCompetitionRes>sendSuccess(tracer));
                        }
                )
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<Competition>> get(GetCompetitionReq req) {
        req.setIsActive(true);
        return this.competitionRepository.get(req)
                .map((data) -> {
                    data.setFkInterestTypeId(jsonUtil.readValue(data.getFkInterestTypeIds(), new TypeReference<List<Long>>() {}));
                    return data;
                })
                .map((data) -> BaseResponse.sendSuccess(tracer, data))
                .switchIfEmpty(Mono.just(BaseResponse.sendSuccess(tracer)))
                .onErrorResume((e) -> {
                    log.error("error occurred with message: {}", e);
                    return Mono.just(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage()));
                });
    }

    @Override
    public Mono<BaseResponse<PaginationRes<Competition>>> getList(GetCompetitionReq req) {
        req.setIsActive(true);
        return this.competitionRepository.getList(req)
                .map((data) -> {
                    data.getList().forEach((competition) -> {
                        competition.setFkInterestTypeId(jsonUtil.readValue(competition.getFkInterestTypeIds(), new TypeReference<List<Long>>() {}));
                    });
                    return data;
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
