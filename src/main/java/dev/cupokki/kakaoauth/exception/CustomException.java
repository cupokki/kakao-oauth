package dev.cupokki.kakaoauth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    private String message;
    private HttpStatus httpStatus;
}
