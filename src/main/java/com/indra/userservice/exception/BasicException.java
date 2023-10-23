package com.indra.userservice.exception;

public class BasicException extends RuntimeException {

    private boolean isNoneError;

    public BasicException(Exception e, String msg) {
        super(msg,e.getCause());
    }

    public BasicException(String msg) {
        super(msg);
        this.isNoneError = false;
    }

    public BasicException(String msg, boolean isNoneError) {
        super(msg);
        this.isNoneError = isNoneError;
    }

    public boolean isNoneError() {
        return isNoneError;
    }


}
