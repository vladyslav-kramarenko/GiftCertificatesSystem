package com.epam.esm.exception;

import java.sql.SQLException;

public class DbException extends SQLException {
    public DbException(String msg) {
        super(msg);
    }
}

