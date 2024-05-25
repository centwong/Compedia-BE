package cent.wong.compedia.cfg;

import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

@Configuration
@AutoConfigureAfter({
        R2dbcAutoConfiguration.class
})
@EnableConfigurationProperties({
        R2dbcProperties.class
})
@Slf4j
@RequiredArgsConstructor
public class R2dbcConfiguration {

    private final R2dbcProperties r2dbcProperties;

    @Bean
    public ConnectionFactory customConnFactory(){

        QueryExecutionInfoFormatter formatter = new QueryExecutionInfoFormatter()
                .showQuery()
                .showBindings()
                .showTransaction()
                .showTime()
                .showSuccess();

        ConnectionFactory conn = ProxyConnectionFactory
                .builder(ConnectionFactories.get(r2dbcProperties.getUrl()))
                .onBeforeQuery((queryInfo) -> log.info("query: {}", formatter.format(queryInfo)))
                .build();
        return conn;
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(){
        return new R2dbcEntityTemplate(customConnFactory());
    }
}
