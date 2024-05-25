package cent.wong.compedia.entity;

import io.micrometer.tracing.Tracer;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseResponse<T> implements Serializable {

    private String transactionId;

    private T data;

    private String errCode;

    private String message;

    private Boolean isSuccess;

    public static <T> BaseResponse<T> sendSuccess(Tracer tracer){
        return sendSuccess(tracer, null, null);
    }

    public static <T> BaseResponse<T> sendSuccess(Tracer tracer, T data){
        return sendSuccess(tracer, data, null);
    }

    public static <T> BaseResponse<T> sendSuccess(Tracer tracer, T data, String message){
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setIsSuccess(true);
        baseResponse.setData(data);
        baseResponse.setMessage(message);
        baseResponse.setErrCode("");
        baseResponse.setTransactionId(tracer.currentTraceContext().context().traceId());
        return baseResponse;
    }

    public static <T> BaseResponse<T> sendError(Tracer tracer, String errCode){
        return sendError(tracer, errCode, null);
    }

    public static <T> BaseResponse<T> sendError(Tracer tracer, String errCode, String message){
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setIsSuccess(false);
        baseResponse.setData(null);
        baseResponse.setMessage(message);
        baseResponse.setErrCode(errCode);
        baseResponse.setTransactionId(tracer.currentTraceContext().context().traceId());
        return baseResponse;
    }
}
