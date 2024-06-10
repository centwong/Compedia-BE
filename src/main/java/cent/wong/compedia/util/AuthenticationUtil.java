package cent.wong.compedia.util;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationUtil {

    public Long extractId(Authentication authentication){
        return Integer.valueOf((int)authentication.getPrincipal()).longValue();
    }
}
