package cent.wong.compedia.app.payment;

import cent.wong.compedia.app.competition.repository.CompetitionRepository;
import cent.wong.compedia.app.competition.repository.CompetitionTrackerRepository;
import cent.wong.compedia.constant.CompetitionPaidStatus;
import cent.wong.compedia.entity.CompetitionTracker;
import cent.wong.compedia.entity.dto.competition.GetCompetitionReq;
import cent.wong.json.JsonUtil;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.scheduler.Schedulers;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    final static class PaymentRequestBody{
        @JsonAlias("transaction_time")
        private String transactionTime;

        @JsonAlias("transaction_status")
        private String transactionStatus;

        @JsonAlias("transaction_id")
        private String transactionId;

        @JsonAlias("status_message")
        private String statusMessage;

        @JsonAlias("status_code")
        private String statusCode;

        @JsonAlias("signature_key")
        private String signatureKey;

        @JsonAlias("payment_type")
        private String paymentType;

        @JsonAlias("order_id")
        private String orderId;

        @JsonAlias("merchant_id")
        private String merchantId;

        @JsonAlias("masked_card")
        private String maskedCard;

        @JsonAlias("gross_amount")
        private String grossAmount;

        @JsonAlias("fraud_status")
        private String fraudStatus;

        private String eci;

        private String currency;

        @JsonAlias("channel_response_message")
        private String channelResponseMessage;

        @JsonAlias("channel_response_code")
        private String channelResponseCode;

        @JsonAlias("card_type")
        private String cardType;

        private String bank;

        @JsonAlias("approval_code")
        private String approvalCode;
    }

    private final CompetitionTrackerRepository competitionTrackerRepository;

    private final CompetitionRepository competitionRepository;

    private final JsonUtil jsonUtil;

    @PostMapping("/payment-notification-handler")
    public void updatePaymentStatus(
            @RequestBody PaymentRequestBody paymentRequestBody
    ){
        log.info("accepting paymentRequestBody from Midtrans: {}", jsonUtil.writeValueAsString(paymentRequestBody));

        CompetitionTracker exampleBody = new CompetitionTracker();
        exampleBody.setUniqueId(paymentRequestBody.orderId);

        this.competitionTrackerRepository
                .findOne(
                        Example.of(exampleBody)
                )
                .flatMap((tracker) -> {
                    GetCompetitionReq getReq = new GetCompetitionReq();
                    getReq.setId(tracker.getCompetitionId());
                    return this.competitionRepository.get(getReq);
                })
                .flatMap((competition) -> {
                    if(paymentRequestBody.transactionStatus.equals("settlement") && paymentRequestBody.fraudStatus.equals("accept")){
                        competition.setCompetitionPaidStatus(
                                CompetitionPaidStatus.PAID.getStatus()
                        );
                    } else {
                        competition.setCompetitionPaidStatus(
                                CompetitionPaidStatus.FAILED_TO_PAY.getStatus()
                        );
                    }

                    return this.competitionRepository.update(competition);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(
                        (data) -> {
                            log.info("success update competition: {}", jsonUtil.writeValueAsString(data));
                        },
                        (err) -> {
                            err.printStackTrace();
                        }
                );

    }
}
