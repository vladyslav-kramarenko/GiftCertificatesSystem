package com.epam.esm.util;

public class Utilities {
    public static boolean validateId(Long id) {
        if (id != null && id >= 0) {
            return true;
        }
        throw new IllegalArgumentException("Wrong ID: " + id);
    }
}
