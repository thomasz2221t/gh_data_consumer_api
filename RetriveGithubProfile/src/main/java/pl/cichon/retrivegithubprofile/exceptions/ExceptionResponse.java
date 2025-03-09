package pl.cichon.retrivegithubprofile.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ExceptionResponse {
    private int status;
    private String message;

    public ExceptionResponse(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
