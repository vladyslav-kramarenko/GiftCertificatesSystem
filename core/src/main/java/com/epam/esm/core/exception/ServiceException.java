package com.epam.esm.core.exception;

import java.sql.SQLException;

public class ServiceException extends SQLException {
    public ServiceException(String msg) {
        super(msg);
    }
}

