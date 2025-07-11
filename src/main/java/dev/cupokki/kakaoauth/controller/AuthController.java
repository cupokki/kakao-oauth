package dev.cupokki.kakaoauth.controller;

import dev.cupokki.kakaoauth.dto.UserLoginRequest;
import dev.cupokki.kakaoauth.dto.UserSignupRequest;
import dev.cupokki.kakaoauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authservice;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        var accessToken= authservice.login(userLoginRequest);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest userSignupRequest) {
        var accessToken= authservice.signup(userSignupRequest);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/auth/social-login")
    public ResponseEntity<?> socialLogin() {
        return ResponseEntity.ok(null);
    }
}
