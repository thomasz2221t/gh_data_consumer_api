package pl.cichon.retrivegithubprofile.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.cichon.retrivegithubprofile.exceptions.ExceptionResponse;
import pl.cichon.retrivegithubprofile.exceptions.ProfileNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ExceptionResponse> handleUserNotFoundException() {
        return new ResponseEntity<>(new ExceptionResponse(404, "User profile not found"), HttpStatus.NOT_FOUND);
    }
}
