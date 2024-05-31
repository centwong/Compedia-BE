package cent.wong.compedia.app.midtrans;

import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.*;
import cent.wong.compedia.util.MidtransUtil;
import com.midtrans.httpclient.SnapApi;
import com.midtrans.httpclient.error.MidtransError;
import io.micrometer.tracing.Tracer;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/public/v1/midtrans")
@RequiredArgsConstructor
@Tag(name = "Payment")
public class MidtransController {

    private final Tracer tracer;

    @GetMapping("/create/payment")
    public Mono<ResponseEntity<BaseResponse<MidtransSnapURI>>> testCreate(){
        try {
            String transactionToken = SnapApi.createTransactionToken(
                    MidtransUtil.generateSnapData(
                            TransactionDetails
                                    .builder()
                                    .grossAmount(200000L)
                                    .orderId(UUID.randomUUID().toString())
                                    .build(),
                            CustomerDetails
                                    .builder()
                                    .email("centwong@gmail.com")
                                    .firstName("CentWong")
                                    .lastName("Cakep")
                                    .phoneNumber("08123123123")
                                    .build()
                    ).convertIntoMap()
            );
            MidtransSnapURI uri = new MidtransSnapURI();
            uri.setRedirectUrl(String.format("https://app.sandbox.midtrans.com/snap/v2/vtweb/%s", transactionToken));
            return Mono.just(ResponseEntity.ok(BaseResponse.sendSuccess(tracer, uri)));
        } catch (MidtransError e) {
            return Mono.just(ResponseEntity.ok(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), e.getMessage())));
        }
    }
}
