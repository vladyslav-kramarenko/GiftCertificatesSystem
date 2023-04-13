package com.epam.esm.util;

import com.epam.esm.model.Tag;

import java.util.List;

import static com.epam.esm.util.CoreConstants.MAX_TAG_NAME_LENGTH;

/**
 * This class provides utility methods for working with tags.
 */
public class TagUtils {
    /**
     * Validates a single tag. Throws an IllegalArgumentException if the tag's name is null or empty,
     * or if the tag's name length is greater than MAX_TAG_NAME_LENGTH.
     *
     * @param tag the tag to validate
     * @throws IllegalArgumentException if the tag is not valid
     */
    public static void validateTag(Tag tag) throws IllegalArgumentException {
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be empty");
        }
        if (tag.name() == null || tag.name().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }
        if (tag.name().length() > MAX_TAG_NAME_LENGTH) {
            throw new IllegalArgumentException("Tag name cannot be bigger than " + MAX_TAG_NAME_LENGTH + " symbols");
        }
    }

    /**
     * Validates a list of tags. Calls validateTag for each tag in the list.
     *
     * @param tags the list of tags to validate
     * @throws IllegalArgumentException if any tag in the list is not valid
     */
    public static void validateTags(List<Tag> tags) throws IllegalArgumentException {
        for (Tag tag : tags) {
            validateTag(tag);
        }
    }
}
