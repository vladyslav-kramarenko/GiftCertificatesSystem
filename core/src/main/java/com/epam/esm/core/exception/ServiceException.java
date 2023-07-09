package com.epam.esm.core.exception;

public class ServiceException extends Exception {
    public ServiceException(String msg) {
        super(msg);
    }

    public ServiceException(String msg, Exception e) {
        super(msg, e);
    }
}

