package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(final Throwable e) {
        return makeApiError(e, "INTERNAL_SERVER_ERROR");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final NullPointerException e) {
        return makeApiError(e, "NOT_FOUND");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final IllegalArgumentException e) {
        return makeApiError(e, "BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MissingServletRequestParameterException e) {
        return makeApiError(e, "BAD_REQUEST");
    }

    private ApiError makeApiError(Throwable e, String status) {
        return new ApiError(status, e.getMessage(), e.getMessage());
    }
}
