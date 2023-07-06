package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MethodArgumentNotValidException e) {
        return makeApiError(e, "BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final MissingServletRequestParameterException e) {
        return makeApiError(e, "BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final DataIntegrityViolationException e) {
        return makeApiError(e, "CONFLICT");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handle(final Throwable e) {
        return makeApiError(e, "INTERNAL_SERVER_ERROR");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handle(final IllegalArgumentException e) {
        return makeApiError(e, "BAD_REQUEST");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handle(final IllegalStateException e) {
        return makeApiError(e, "CONFLICT");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handle(final NotFoundException e) {
        return makeApiError(e, "NOT_FOUND");
    }

    private ApiError makeApiError(Throwable e, String status) {
        return new ApiError(status, e.getMessage(), e.getCause());
    }
}
