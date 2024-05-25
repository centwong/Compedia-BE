package cent.wong.compedia;

import cent.wong.CentWongJsonConfiguration;
import cent.wong.CentWongR2dbcConfiguration;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import reactor.core.publisher.Hooks;

@SpringBootApplication(scanBasePackageClasses = {
		CentWongJsonConfiguration.class,
		CentWongR2dbcConfiguration.class
})
@EnableCaching
@OpenAPIDefinition(
		info = @Info(
				title = "Compedia App",
				description = "BE implementation for Gemastik 2024 competition",
				contact = @Contact(
						name = "Vinncent Alexander Wong",
						email = "vinncentwong1@gmail.com"
				)
		)
)
@SecuritySchemes({
		@SecurityScheme(
				name = "JWT",
				in = SecuritySchemeIn.HEADER,
				bearerFormat = "JWT",
				scheme = "bearer",
				type = SecuritySchemeType.HTTP,
				description = "Bearer token"
		)
})
public class CompediaApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompediaApplication.class, args);
	}

	@PostConstruct
	public void enableContextPropagation(){
		Hooks.enableAutomaticContextPropagation();
	}
}
