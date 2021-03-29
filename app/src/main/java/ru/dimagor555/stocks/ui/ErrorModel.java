package ru.dimagor555.stocks.ui;

import android.content.Context;
import ru.dimagor555.stocks.R;
import ru.dimagor555.stocks.data.remote.exception.ApiLimitReachedException;
import ru.dimagor555.stocks.data.remote.exception.NetworkErrorException;
import ru.dimagor555.stocks.data.remote.exception.UnknownErrorException;

public class ErrorModel {
    private final int titleInt;
    private final String message;
    private final int messageInt;

    public static ErrorModel newErrorModel(Exception e) {
        if (e instanceof NetworkErrorException) {
            return new ErrorModel(((NetworkErrorException) e));
        } else if (e instanceof UnknownErrorException) {
            return new ErrorModel(((UnknownErrorException) e));
        } else if (e instanceof ApiLimitReachedException) {
          return new ErrorModel(((ApiLimitReachedException) e));
        } else {
            throw new IllegalArgumentException("Unknown type of exception " + e.getClass());
        }
    }

    private ErrorModel(NetworkErrorException error) {
        this(R.string.network_error, R.string.network_error_message);
    }

    private ErrorModel(UnknownErrorException error) {
        this(R.string.unknown_error, error.getMessage());
    }

    public ErrorModel(ApiLimitReachedException e) {
        this(R.string.api_limit_error, R.string.api_limit_error_message);
    }

    private ErrorModel(int titleInt, String message) {
        this.titleInt = titleInt;
        this.message = message;
        messageInt = -1;
    }

    private ErrorModel(int titleInt, int messageInt) {
        this.titleInt = titleInt;
        message = null;
        this.messageInt = messageInt;
    }

    public String getTitle(Context context) {
        return context.getString(titleInt);
    }

    public String getMessage(Context context) {
        if (message == null) {
            return context.getString(messageInt);
        } else {
            return message;
        }
    }
}
