package io.chudzik.recruitment.budfox.model;

import org.springframework.http.HttpStatus;

import java.util.Optional;

public class ErrorMessage {

    private int code;
    private String message;
    private String details;


    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorMessage(HttpStatus badRequest, String message) {
        this(badRequest.value(), message);
    }


    public ErrorMessage(int code, String message, String details) {
        this(code, message);
        this.details = details;
    }

    public ErrorMessage(HttpStatus httpStatus, String message, String details) {
        this(httpStatus.value(), message, details);
    }



    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return Optional.ofNullable(details)
            .orElseGet(String::new);
    }


    @Override
    public String toString() {
        String toStringMessage = String.format("[%d] %s", code, message);
        if (details != null) {
            toStringMessage = String.format("%s: %s", toStringMessage, details);
        }
        return toStringMessage;
    }

}
