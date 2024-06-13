package cent.wong.compedia.app.competition.controller;

import cent.wong.compedia.app.competition.service.CompetitionService;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.Competition;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.dto.competition.GetCompetitionReq;
import cent.wong.compedia.entity.dto.competition.GetCompetitionRes;
import cent.wong.compedia.entity.dto.competition.SaveUpdateCompetitionReq;
import cent.wong.compedia.entity.dto.competition.SaveUpdateCompetitionRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/private")
@RequiredArgsConstructor
@SecurityRequirement(name = "JWT")
@Tag(name = "Competition")
public class CompetitionController {

    private final CompetitionService competitionService;

    @PostMapping(
            value = "/v1/competition/save",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(description = "Save competition")
    public Mono<ResponseEntity<BaseResponse<SaveUpdateCompetitionRes>>> save(
            Authentication authentication,
            @RequestPart("body") @Valid SaveUpdateCompetitionReq req,
            @RequestPart("file") Mono<FilePart> file
    ){
        return this.competitionService.save(authentication, req, file)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/competition/get")
    @Operation(description = "Get competition")
    public Mono<ResponseEntity<BaseResponse<GetCompetitionRes>>> get(
            @RequestBody GetCompetitionReq req
            ){
        return this.competitionService.get(req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/competition/get/list")
    @Operation(description = "Get list competition")
    public Mono<ResponseEntity<BaseResponse<PaginationRes<GetCompetitionRes>>>> getList(
            @RequestBody GetCompetitionReq req
    ){
        return this.competitionService.getList(req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping(
            value = "/v1/competition/update/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(description = "Update competition")
    public Mono<ResponseEntity<BaseResponse<Object>>> update(
            Authentication authentication,
            @PathVariable("id") Long id,
            @RequestPart(value = "body", required = false) SaveUpdateCompetitionReq req,
            @RequestPart(value = "file", required = false) Mono<FilePart> file
    ){
        GetCompetitionReq getCompetitionReq = new GetCompetitionReq();
        getCompetitionReq.setId(id);
        return this.competitionService.update(authentication, getCompetitionReq, req, file)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/competition/get/delete/{id}")
    @Operation(description = "Delete competition")
    public Mono<ResponseEntity<BaseResponse<Object>>> delete(
            Authentication authentication,
            @PathVariable("id") Long id
    ){
        GetCompetitionReq getCompetitionReq = new GetCompetitionReq();
        getCompetitionReq.setId(id);
        return this.competitionService.delete(authentication, getCompetitionReq)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @PostMapping("/v1/competition/get/activate/{id}")
    @Operation(description = "Activate competition")
    public Mono<ResponseEntity<BaseResponse<Object>>> activate(
            Authentication authentication,
            @PathVariable("id") Long id
    ){
        GetCompetitionReq getCompetitionReq = new GetCompetitionReq();
        getCompetitionReq.setId(id);
        return this.competitionService.activate(authentication, getCompetitionReq)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
