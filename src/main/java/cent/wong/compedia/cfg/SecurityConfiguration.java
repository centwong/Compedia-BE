package cent.wong.compedia.cfg;

import cent.wong.compedia.security.filter.JwtFilter;
import cent.wong.compedia.security.manager.NoOpManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final NoOpManager noOpManager;

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity httpSecurity){
        return httpSecurity
                .csrf((c) -> c.disable())
                .cors((c) -> {
                    c.configurationSource(
                            (r) -> {
                                List<String> all = List.of("*");
                                CorsConfiguration configuration = new CorsConfiguration();
                                configuration.setAllowedOrigins(all);
                                configuration.setAllowedMethods(all);
                                configuration.setAllowedHeaders(all);
                                return configuration;
                            }
                    );
                })
                .addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHORIZATION)
                .httpBasic((c) -> c.disable())
                .authorizeExchange((c) -> {
                    c.pathMatchers("/private/**").authenticated()
                    .anyExchange().permitAll();
                })
                .authenticationManager(noOpManager)
                .build();
    }

}
