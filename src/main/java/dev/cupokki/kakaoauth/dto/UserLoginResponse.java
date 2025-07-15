package dev.cupokki.kakaoauth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import dev.cupokki.kakaoauth.entity.SocialType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserLoginResponse {
    private String accessToken;
    private SocialType socialType;
    private String socialId;
    @JsonProperty("isRegistered")
    private boolean isRegistered;

}
