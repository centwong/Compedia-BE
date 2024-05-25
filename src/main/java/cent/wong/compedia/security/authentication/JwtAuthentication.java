package cent.wong.compedia.security.authentication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication extends UsernamePasswordAuthenticationToken {
    public JwtAuthentication(Object principal, Object credentials) {
        super(principal, credentials);
    }

    public JwtAuthentication(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}
