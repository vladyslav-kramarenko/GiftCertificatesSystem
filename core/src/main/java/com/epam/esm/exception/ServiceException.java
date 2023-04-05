package com.epam.esm.exception;

import java.sql.SQLException;

public class ServiceException extends SQLException {
    public ServiceException(String msg) {
        super(msg);
    }
}

