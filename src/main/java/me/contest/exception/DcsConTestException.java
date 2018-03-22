package me.contest.exception;

/**
 * Created by shilong.zhang on 2017/12/18.
 */
public class DcsConTestException extends RuntimeException {
    private Throwable throwable;

    private String message;

    private int httpCode;

    public DcsConTestException(String message, int httpCode) {
        super(message);
        this.message = message;
        this.httpCode = httpCode;
    }
}
