package dev.cupokki.kakaoauth.security;

import dev.cupokki.kakaoauth.entity.User;
import dev.cupokki.kakaoauth.repository.UserRepository;
import dev.cupokki.kakaoauth.service.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.JstlUtils;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements AuthenticationProvider {

    private final static Long TOKEN_EXP = 60 * 15L;
    private final String secretKey = "1231234guihbasd89y12h2b3kjasjd09u12h3j12312asoifoih123";
    private final CustomUserDetailsService userDetailsService;

    public String generate(Long userId) {
        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .setExpiration(Date.from(Instant.now().plusSeconds(TOKEN_EXP)))
                .setSubject(userId.toString())
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJwt(token)
                .getBody();
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var principal = userDetailsService.loadUserByUsername(authentication.getCredentials().toString());
        return new JwtAuthenticationToken(null);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
