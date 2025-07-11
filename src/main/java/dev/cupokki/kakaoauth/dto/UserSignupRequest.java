package dev.cupokki.kakaoauth.dto;

import dev.cupokki.kakaoauth.entity.SocialType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserSignupRequest {
    private String username;
    private String email;
    private String password;
    private String confirmPassword;
    private SocialType socialType;
    private String socialId;
}
