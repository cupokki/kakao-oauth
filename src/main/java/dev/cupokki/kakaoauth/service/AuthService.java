package dev.cupokki.kakaoauth.service;

import dev.cupokki.kakaoauth.dto.UserLoginRequest;
import dev.cupokki.kakaoauth.dto.UserSignupRequest;
import dev.cupokki.kakaoauth.entity.SocialType;
import dev.cupokki.kakaoauth.entity.User;
import dev.cupokki.kakaoauth.repository.UserRepository;
import dev.cupokki.kakaoauth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(UserLoginRequest userLoginRequest) {
        var foundUser = userRepository.findByUsername(userLoginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("아이디 혹은 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(userLoginRequest.getPassword(), foundUser.getPassword())) {
            throw new RuntimeException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.generate(foundUser.getId());
    }

    public String signup(UserSignupRequest userSignupRequest) {
        if (userRepository.existsByUsername(userSignupRequest.getUsername())) {
            throw new RuntimeException("사용할 수 없는 아이디입니다.");
        }

        if (userRepository.existsByEmail(userSignupRequest.getEmail())) {
            throw new RuntimeException("사용할 수 없는 이메일입니다.");
        }

        var savedUser = userRepository.save(User.builder()
                .username(userSignupRequest.getUsername())
                .password(passwordEncoder.encode(userSignupRequest.getPassword()))
                .email(userSignupRequest.getEmail())
                .build());

        return jwtTokenProvider.generate(savedUser.getId());
    }

    public String socialLogin(UserLoginRequest userLoginRequest) {
        switch(userLoginRequest.getSocialType()) {
            case SocialType.KAKAO:
            default:
        }
        return "dummy";
    }
}
