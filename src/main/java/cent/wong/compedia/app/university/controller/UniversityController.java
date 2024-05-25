package cent.wong.compedia.app.university.controller;

import cent.wong.compedia.app.university.service.UniversityService;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.PaginationRes;
import cent.wong.compedia.entity.University;
import cent.wong.compedia.entity.dto.university.GetUniversityReq;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/public/v1")
@RequiredArgsConstructor
@Tag(name = "University")
public class UniversityController {

    private final UniversityService universityService;

    @Operation(description = "Get single university")
    @PostMapping("/university/get")
    public Mono<ResponseEntity<BaseResponse<University>>> get(
            @RequestBody GetUniversityReq req
            ){
        return this.universityService.get(req)
                .map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Operation(description = "Get list of university")
    @PostMapping("/university/get/list")
    public Mono<ResponseEntity<BaseResponse<PaginationRes<University>>>> getList(
            @RequestBody GetUniversityReq req
    ){
        return this.universityService.getList(req).map(ResponseEntity::ok)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
