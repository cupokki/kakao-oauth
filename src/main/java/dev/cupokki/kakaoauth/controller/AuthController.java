package dev.cupokki.kakaoauth.controller;

import dev.cupokki.kakaoauth.service.Authservice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final Authservice authservice;

    @PostMapping("/auth/login")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok(null);
    }
}
