package ru.dimagor555.stocks.data.remote.exception;

/**
 * ApiLimitReachedException wraps http 429 error code, which means api limit reached
 */
public class ApiLimitReachedException extends RuntimeException {
}
