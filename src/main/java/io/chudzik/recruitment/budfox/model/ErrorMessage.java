package io.chudzik.recruitment.budfox.model;

import com.google.common.base.Strings;


public class ErrorMessage {

    private int code;
    private String message;
    private String details;

    public ErrorMessage(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorMessage(int code, String message, String details) {
        this(code, message);
        this.details = details;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return Strings.nullToEmpty(details);
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
