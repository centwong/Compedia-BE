package cent.wong.compedia.app.interest.controller;

import cent.wong.compedia.app.interest.service.InterestService;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.entity.InterestRangePrice;
import cent.wong.compedia.entity.InterestTime;
import cent.wong.compedia.entity.InterestType;
import cent.wong.compedia.entity.dto.interest.SaveInterestReq;
import cent.wong.compedia.entity.dto.interest.SaveInterestRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/private/v1/interest")
@Tag(name = "Interest")
@SecurityRequirement(name = "JWT")
public class InterestController {

    private final InterestService interestService;

    @PostMapping
    @Operation(description = "Save interest")
    public Mono<ResponseEntity<BaseResponse<SaveInterestRes>>> save(
            Authentication authentication,
            @RequestBody @Valid SaveInterestReq req
            ){
        return this.interestService.save(authentication, req)
                .map(ResponseEntity::ok);
    }

    @PostMapping("/type/list")
    @Operation(description = "Get all interest type")
    public Mono<ResponseEntity<BaseResponse<List<InterestType>>>> getAllInterestType(){
        return this.interestService.getAllInterestType()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/time/list")
    @Operation(description = "Get all interest time")
    public Mono<ResponseEntity<BaseResponse<List<InterestTime>>>> getAllInterestTime(){
        return this.interestService.getAllInterestTime()
                .map(ResponseEntity::ok);
    }

    @PostMapping("/range-price/list")
    @Operation(description = "Get all interest range price")
    public Mono<ResponseEntity<BaseResponse<List<InterestRangePrice>>>> getAllInterestRangePrice(){
        return this.interestService.getAllInterestRangePrice()
                .map(ResponseEntity::ok);
    }
}
