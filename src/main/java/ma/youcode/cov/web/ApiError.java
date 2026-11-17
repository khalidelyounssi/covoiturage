package ma.youcode.cov.web;

import org.springframework.http.HttpStatus;

public final class ApiError extends RuntimeException {
    private final HttpStatus status;

    private ApiError(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus status() { return status; }
    public static ApiError invalide(String message) { return new ApiError(HttpStatus.BAD_REQUEST, message); }
    public static ApiError interdit(String message) { return new ApiError(HttpStatus.FORBIDDEN, message); }
    public static ApiError absent(String message) { return new ApiError(HttpStatus.NOT_FOUND, message); }
    public static ApiError conflit(String message) { return new ApiError(HttpStatus.CONFLICT, message); }
}