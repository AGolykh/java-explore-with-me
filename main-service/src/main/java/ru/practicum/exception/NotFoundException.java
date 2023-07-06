package ru.practicum.exception;

public class NotFoundException extends NullPointerException {
    public NotFoundException(String object, Long objectId) {
        super(String.format("%s id %d not found.", object, objectId));
    }
}
