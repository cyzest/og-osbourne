package com.cyzest.ogosbourne.model;

public class ResponseBody<T> {

    private int code;
    private String message;
    private T extra;

    public ResponseBody() {}

    public ResponseBody(final int code, final String message) {
        this.code = code;
        this.message = message;
        this.extra = null;
    }

    public ResponseBody(final int code, final String message, final T extra) {
        this.code = code;
        this.message = message;
        this.extra = extra;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getExtra() {
        return extra;
    }
}
