package ru.practicum.shareit.server.exception;

public class WrongOwnerException extends RuntimeException {
    public WrongOwnerException(String message) {
        super(message);
    }
}
