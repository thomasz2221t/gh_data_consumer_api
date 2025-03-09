package pl.cichon.retrivegithubprofile.exceptions;

import lombok.Getter;

@Getter
public class ProfileNotFoundException extends Exception {
    String errorMessage;

    public ProfileNotFoundException(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
