package cent.wong.compedia.security.filter;

import cent.wong.compedia.constant.ErrorCode;
import cent.wong.compedia.entity.BaseResponse;
import cent.wong.compedia.security.authentication.JwtAuthentication;
import cent.wong.json.JsonUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter implements WebFilter {

    private final Tracer tracer;

    private final JsonUtil jsonUtil;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest req = exchange.getRequest();
        ServerHttpResponse res = exchange.getResponse();

        res.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        res.setStatusCode(HttpStatus.OK);

        DataBufferFactory factory = res.bufferFactory();

        String currentPath = req.getURI().getPath();
        if(currentPath.contains("private")){
            List<String> headers = req.getHeaders().get(HttpHeaders.AUTHORIZATION);
            if(headers == null || headers.isEmpty()){
                BaseResponse<?> baseResponse = BaseResponse.sendError(tracer, ErrorCode.AUTHORIZATION_HEADER_REQUIRED.getErrCode(), ErrorCode.AUTHORIZATION_HEADER_REQUIRED.getMessage());
                return res.writeWith(writeResponse(factory, baseResponse));
            }

            String header = headers.get(0);
            if(header.startsWith("Bearer")){
                header = header.substring(7);
                try{
                    Jws<Claims> jws = Jwts
                            .parserBuilder()
                            .setSigningKey(Decoders.BASE64.decode(jwtSecret))
                            .build()
                            .parseClaimsJws(header);
                    Claims body = jws.getBody();
                    Authentication authentication = new JwtAuthentication(
                            body.getId(),
                            null,
                            List.of(
                                    new SimpleGrantedAuthority(String.format("ROLE_%s", body.get("role")))
                            )
                    );

                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                } catch (Exception ex){
                    log.error("error occurred with message: {}", ex);
                    BaseResponse<?> baseResponse = BaseResponse.sendError(tracer, ErrorCode.PARSE_JWT_ERROR.getErrCode(), ex.getMessage());
                    return res.writeWith(writeResponse(factory, baseResponse));
                }
            } else {
                BaseResponse<?> baseResponse = BaseResponse.sendError(tracer, ErrorCode.JWT_FORMAT_INVALID.getErrCode(), ErrorCode.JWT_FORMAT_INVALID.getMessage());
                return res.writeWith(writeResponse(factory, baseResponse));
            }
        } else {
            return chain.filter(exchange);
        }
    }

    private Publisher<DataBuffer> writeResponse(DataBufferFactory factory, Object data){
        return Mono.just(factory.wrap(jsonUtil.writeValueAsBytes(data)));
    }
}
