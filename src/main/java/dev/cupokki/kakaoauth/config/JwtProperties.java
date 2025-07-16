package dev.cupokki.kakaoauth.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Getter
public class JwtProperties {
    private String secretKey;
    private Long tokenExp;
}
