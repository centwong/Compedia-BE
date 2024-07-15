package cent.wong.compedia.app.user.controller;

import cent.wong.compedia.app.user.service.MentorService;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.MentorData;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.mentor.GetMentorDataReq;
import cent.wong.compedia.entity.dto.mentor.GetMentorDetailRes;
import cent.wong.compedia.entity.dto.mentor.GetMentorRes;
import cent.wong.compedia.entity.dto.user.GetUserReq;
import cent.wong.compedia.entity.dto.user.MentorUpdateApprovalReq;
import cent.wong.compedia.entity.dto.user.MentorUpdateDetailReq;
import cent.wong.compedia.entity.dto.user.SaveMentorDataReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@Tag(name = "Mentor")
@RequestMapping("/private")
@SecurityRequirement(name = "JWT")
public class MentorController {

    private final MentorService mentorService;

    @PostMapping("/v1/mentor/save")
    @Operation(description = "Save mentor data")
    public Mono<ResponseEntity<BaseResponse<MentorData>>> save(
            Authentication authentication,
            @RequestBody
            @Valid SaveMentorDataReq req
            ){
        return this.mentorService.save(authentication, req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/mentor/data/get")
    @Operation(description = "Get mentor data")
    public Mono<ResponseEntity<BaseResponse<GetMentorDataReq>>> get(
            Authentication authentication
    ){
        return this.mentorService.get(authentication)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping("/v1/mentor/update/approval")
    @Operation(description = "Update mentor approval status")
    public Mono<ResponseEntity<BaseResponse<Object>>> updateApproval(
            Authentication authentication,
            @RequestBody
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "WAITING_FOR_APPROVAL",
                                            value = "{\"mentorDataId\":4,\"status\":\"WAITING_FOR_APPROVAL\"}"
                                    ),
                                    @ExampleObject(
                                            name = "ACCEPTED",
                                            value = "{\"mentorDataId\":4,\"status\":\"ACCEPTED\"}"
                                    ),
                                    @ExampleObject(
                                            name = "REJECTED",
                                            value = "{\"mentorDataId\":4,\"status\":\"REJECTED\"}"
                                    )
                            }
                    )
            )
            @Valid MentorUpdateApprovalReq req
    ){
        return this.mentorService.updateApproval(authentication, req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/mentor/list")
    @Operation(description = "Get list mentor")
    public Mono<ResponseEntity<BaseResponse<PaginationRes<GetMentorRes>>>> getList(
            Authentication authentication
            ){
        return this.mentorService.getList(authentication)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/mentor/detail")
    @Operation(description = "Get detail mentor")
    public Mono<ResponseEntity<BaseResponse<GetMentorDetailRes>>> get(
            @RequestBody GetUserReq req
    ){
        return this.mentorService.getMentorDetail(req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/mentor/data/update/detail")
    @Operation(description = "Update mentor detail after success save it's data and approved")
    public Mono<ResponseEntity<BaseResponse<Object>>> updateMentorDetail(
            Authentication authentication,
            @RequestBody @Valid MentorUpdateDetailReq req
            ){
        return this.mentorService.updateMentorDetail(authentication, req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
