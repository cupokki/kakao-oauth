package dev.cupokki.kakaoauth.security;

import dev.cupokki.kakaoauth.entity.User;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String jwt;

    private User user;

    public JwtAuthenticationToken(String jwt) {
        super(null);
        this.jwt = jwt;
        this.user = null;
        setAuthenticated(false); // 인증 전

    }

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities, User user) {
        super(authorities);
        this.jwt = null;
        this.user = user;
        setAuthenticated(true); // 인증 완료
    }


    @Override
    public Object getCredentials() {
        return this.jwt;
    }

    @Override
    public Object getPrincipal() {
        return this.user;
    }
}
