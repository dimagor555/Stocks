package ru.dimagor555.stocks.data.remote.exception;

/**
 * UnknownException wraps all other exceptions from network requests,
 * except NetworkException and ApiLimitReachedException
 */
public class UnknownErrorException extends RuntimeException {
    public UnknownErrorException(String message) {
        super(message);
    }
}
