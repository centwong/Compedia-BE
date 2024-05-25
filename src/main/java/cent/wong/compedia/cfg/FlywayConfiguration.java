package cent.wong.compedia.cfg;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        DataSourceProperties.class
})
public class FlywayConfiguration {

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSourceProperties properties){
        return Flyway
                .configure()
                .baselineOnMigrate(true)
                .locations("classpath:/db/migration")
                .dataSource(
                        properties.getUrl(),
                        properties.getUsername(),
                        properties.getPassword()
                )
                .load();
    }
}
