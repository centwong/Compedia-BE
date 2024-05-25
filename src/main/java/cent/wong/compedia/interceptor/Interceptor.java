package cent.wong.compedia.interceptor;

import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.BaseResponse;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class Interceptor {

    private final Tracer tracer;

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<BaseResponse<?>>> handleException(WebExchangeBindException ex){
        log.error("error occurred with message: {}", ex);
        StringBuilder message = new StringBuilder();
        ex.getAllErrors().forEach((e) -> message.append(e.getDefaultMessage()).append(", "));
        return Mono.just(ResponseEntity.ok(BaseResponse.sendError(tracer, ErrorCode.BAD_REQUEST.getErrCode(), message.substring(0, message.length() - 2))));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<BaseResponse<?>>> handleException(Exception ex){
        log.error("error occurred with message: {}", ex);
        return Mono.just(ResponseEntity.ok(BaseResponse.sendError(tracer, ErrorCode.INTERNAL_SERVER_ERROR.getErrCode(), ex.getMessage())));
    }
}
