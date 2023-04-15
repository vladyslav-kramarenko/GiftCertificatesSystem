package com.epam.esm.core.exception;

import java.sql.SQLException;

public class DbException extends SQLException {
    public DbException(String msg) {
        super(msg);
    }
}

