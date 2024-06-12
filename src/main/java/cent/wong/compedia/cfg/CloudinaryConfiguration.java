package cent.wong.compedia.cfg;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfiguration {

    @Value("${cloudinary.api-key}")
    private String cloudinaryApiKey;

    @Value("${cloudinary.api-secret}")
    private String cloudinaryApiSecret;

    @Value("${cloudinary.name}")
    private String cloudinaryName;

    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(
                Map.ofEntries(
                        Map.entry("cloud_name", cloudinaryName),
                        Map.entry("api_key", cloudinaryApiKey),
                        Map.entry("api_secret", cloudinaryApiSecret)
                )
        );
    }
}
