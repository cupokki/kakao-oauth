package dev.cupokki.kakaoauth.dto;

import dev.cupokki.kakaoauth.entity.SocialType;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserLoginRequest {

    private String username;
    private String password;
    private SocialType socialType;
    private String code;
    private Boolean isLongTerm;
}
