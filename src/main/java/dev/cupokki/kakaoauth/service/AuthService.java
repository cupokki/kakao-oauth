package dev.cupokki.kakaoauth.service;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;
import dev.cupokki.kakaoauth.dto.KakaoTokenResponse;
import dev.cupokki.kakaoauth.dto.UserLoginRequest;
import dev.cupokki.kakaoauth.dto.UserLoginResponse;
import dev.cupokki.kakaoauth.dto.UserSignupRequest;
import dev.cupokki.kakaoauth.entity.SocialAccount;
import dev.cupokki.kakaoauth.entity.SocialType;
import dev.cupokki.kakaoauth.entity.User;
import dev.cupokki.kakaoauth.exception.CustomException;
import dev.cupokki.kakaoauth.repository.SocialAccountRepository;
import dev.cupokki.kakaoauth.repository.UserRepository;
import dev.cupokki.kakaoauth.security.JwtTokenProvider;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SocialAccountRepository socialAccountRepository;

    public String login(UserLoginRequest userLoginRequest) {
        var foundUser = userRepository.findByUsername(userLoginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("아이디 혹은 비밀번호가 일치하지 않습니다."));

        if (!passwordEncoder.matches(userLoginRequest.getPassword(), foundUser.getPassword())) {
            throw new RuntimeException("아이디 혹은 비밀번호가 일치하지 않습니다.");
        }

        return jwtTokenProvider.generate(foundUser.getId());
    }

    @Transactional
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

        var savedSocialAccount = socialAccountRepository.save(SocialAccount.builder()
                .user(savedUser)
                .socialType(userSignupRequest.getSocialType())
                .socialId(userSignupRequest.getSocialId())
                .build());

        savedUser.addSocialAcount(savedSocialAccount);

        return jwtTokenProvider.generate(savedUser.getId());
    }

    /**
     * 소셜 로그인
     * @param userLoginRequest
     * @return
     */
    public UserLoginResponse socialLogin(UserLoginRequest userLoginRequest) {
        String token = "";
        boolean isRegistered = true;
        String socialId = "";
        switch(userLoginRequest.getSocialType()) {
            case SocialType.KAKAO:
                var kakaoTokenResponse = getKakaoToken(userLoginRequest.getCode());
                socialId = getKakaoUserId(kakaoTokenResponse.getAccessToken());
                log.info(socialId);
                var socialAccount = socialAccountRepository.findBySocialId(socialId);
//                        .orElseThrow(() -> new CustomException("미가입자", HttpStatus.NO_CONTENT));
                if(socialAccount.isEmpty()) {
                    isRegistered = false;
                    break;
                }
                var user = socialAccount.get().getUser();
                log.info(user.getId().toString());
                token = jwtTokenProvider.generate(user.getId());
            default:
        }
        return UserLoginResponse.builder()
                .isRegistered(isRegistered)
                .accessToken(token)
                .socialType(userLoginRequest.getSocialType())
                .socialId(socialId)
                .build();
    }

    public KakaoTokenResponse getKakaoToken(String code) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set(HttpHeaders.ACCEPT_CHARSET, "UTF-8");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "22eab5145311a4edbcc3e061de0b8a28");
        body.add("redirect_uri", "http://localhost:8080/kakao-oauth.html");
        body.add("code",code);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(url, request, KakaoTokenResponse.class)
                .getBody();
    }

    public String getKakaoUserId(String token) {
//        // 1. 토큰 파싱
//        SignedJWT jwt = SignedJWT.parse(response.getBody().getIdToken());
//        String kid = jwt.getHeader().getKeyID();
//
//        // 2. JWKS 가져오기
//        URL jwksUrl = new URL("https://kauth.kakao.com/.well-known/jwks.json");
//        JWKSet jwkSet = JWKSet.load(jwksUrl);
//
//        // 3. kid에 해당하는 키 찾기
//        JWK jwk = jwkSet.getKeyByKeyId(kid);
//        RSAPublicKey publicKey = jwk.toRSAKey().toRSAPublicKey();
//
//        // 4. 서명 검증
//        JWSVerifier verifier = new RSASSAVerifier(publicKey);
//        boolean isValid = jwt.verify(verifier);
//        System.out.println("서명 유효성: " + isValid);
//
//        if (isValid) {
//            String sub = jwt.getJWTClaimsSet().getSubject();
//            String email = jwt.getJWTClaimsSet().getStringClaim("email");
//            System.out.println("사용자 ID(sub): " + sub);
//            System.out.println("이메일: " + email);
//        }
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> response = new RestTemplate().postForEntity(
                "https://kapi.kakao.com/v2/user/me",
                new HttpEntity<>(headers),
                Map.class
        );

        return response.getBody().get("id").toString();
    }
}
