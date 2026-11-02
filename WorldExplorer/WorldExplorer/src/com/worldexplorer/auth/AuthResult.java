package com.worldexplorer.auth;

/** Outcome of a login attempt. */
public class AuthResult {
    public enum Status { OK, EMPTY_INPUT, BAD_CREDENTIALS, LOCKED }

    private final Status status;
    private final String message;
    private final String username;

    private AuthResult(Status status, String message, String username) {
        this.status = status;
        this.message = message;
        this.username = username;
    }

    static AuthResult ok(String username) { return new AuthResult(Status.OK, "", username); }
    static AuthResult fail(Status s, String message) { return new AuthResult(s, message, null); }

    public boolean isSuccess() { return status == Status.OK; }
    public Status getStatus() { return status; }
    public String getMessage() { return message; }
    public String getUsername() { return username; }
}
