package cent.wong.compedia;

import cent.wong.CentWongJsonConfiguration;
import cent.wong.CentWongR2dbcConfiguration;
import com.midtrans.Midtrans;
import dev.langchain4j.spring.LangChain4jAutoConfig;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Hooks;

@SpringBootApplication(scanBasePackageClasses = {
		CentWongJsonConfiguration.class,
		CentWongR2dbcConfiguration.class,
		LangChain4jAutoConfig.class,
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
		),
		servers = {
				@Server(
						url = "http://localhost:8000",
						description = "LOCAL"
				),
				@Server(
						url = "https://satyr-profound-broadly.ngrok-free.app",
						description = "NGROK"
				)
		}
)
@EnableReactiveMethodSecurity
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

	@Value("${midtrans.client-key}")
	private String midtransClientKey;

	@Value("${midtrans.secret-key}")
	private String midtransServerKey;

	public static void main(String[] args) {
		SpringApplication.run(CompediaApplication.class, args);
	}

	@PostConstruct
	public void enableContextPropagation(){
		Hooks.enableAutomaticContextPropagation();

		Midtrans.isProduction = false; // just use sandbox for now
		Midtrans.serverKey = this.midtransServerKey;
		Midtrans.clientKey = this.midtransClientKey;
	}
}
