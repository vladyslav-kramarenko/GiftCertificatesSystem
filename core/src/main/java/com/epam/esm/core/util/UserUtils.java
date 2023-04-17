package com.epam.esm.core.util;

import com.epam.esm.core.entity.User;

import static com.epam.esm.core.util.CoreConstants.*;

/**
 * This class provides utility methods for working with tags.
 */
public class UserUtils {
    /**
     * Validates a single tag. Throws an IllegalArgumentException if the tag's name is null or empty,
     * or if the tag's name length is greater than MAX_TAG_NAME_LENGTH.
     *
     * @param user the tag to validate
     * @throws IllegalArgumentException if the tag is not valid
     */
    public static void validateUser(User user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be empty");
        }
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("User First Name cannot be empty");
        }
        if (user.getFirstName().length() > MAX_USER_FIRST_NAME_LENGTH) {
            throw new IllegalArgumentException("User First Name cannot be bigger than " + MAX_USER_FIRST_NAME_LENGTH + " symbols");
        }
        if (user.getLastName() != null && user.getLastName().length() > MAX_USER_LAST_NAME_LENGTH) {
            throw new IllegalArgumentException("User First Name cannot be bigger than " + MAX_USER_LAST_NAME_LENGTH + " symbols");
        }
    }
}
