package io.chudzik.recruitment.budfox.commons.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter
@AllArgsConstructor
public class ErrorMessage {

    private int code;
    private String message;
    private String details;


    public ErrorMessage(int code, String message) {
        this(code, message, null);
    }

    public ErrorMessage(HttpStatus badRequest, String message) {
        this(badRequest.value(), message);
    }


    public ErrorMessage(HttpStatus httpStatus, String message, String details) {
        this(httpStatus.value(), message, details);
    }

}
