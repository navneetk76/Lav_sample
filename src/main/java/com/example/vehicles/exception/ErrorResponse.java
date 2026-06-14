package com.example.vehicles.exception;

import java.time.LocalDateTime;
import java.util.List;

/** Uniform JSON error body returned by the global handler. */
public class ErrorResponse {

    private int status;
    private String error;
    private List<String> messages;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, List<String> messages) {
        this.status = status;
        this.error = error;
        this.messages = messages;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public List<String> getMessages() { return messages; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
