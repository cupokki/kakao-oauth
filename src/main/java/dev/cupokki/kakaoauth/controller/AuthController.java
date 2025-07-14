package dev.cupokki.kakaoauth.controller;

import com.nimbusds.jose.JOSEException;
import dev.cupokki.kakaoauth.dto.UserLoginRequest;
import dev.cupokki.kakaoauth.dto.UserSignupRequest;
import dev.cupokki.kakaoauth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.ParseException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest userLoginRequest) {
        var accessToken= authService.login(userLoginRequest);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/auth/signup")
    public ResponseEntity<?> signup(@RequestBody UserSignupRequest userSignupRequest) {
        log.info("{}", userSignupRequest);
        var accessToken= authService.signup(userSignupRequest);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }

    @PostMapping("/auth/social-login")
    public ResponseEntity<?> socialLogin(@RequestBody UserLoginRequest userLoginRequest) throws ParseException, IOException, JOSEException {
        var accessToken = authService.socialLogin(userLoginRequest);
        return ResponseEntity.ok(Map.of("accessToken", accessToken));
    }
}
