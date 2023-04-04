package com.epam.esm.util;

import com.epam.esm.model.Tag;

import java.util.List;

import static com.epam.esm.util.Constants.MAX_TAG_NAME_LENGTH;

public class TagUtils {
    public static void validateTag(Tag tag) throws IllegalArgumentException {
        if (tag.getName() == null || tag.getName().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }
        if (tag.getName().length() > MAX_TAG_NAME_LENGTH) {
            throw new IllegalArgumentException("Tag name cannot be bigger than " + MAX_TAG_NAME_LENGTH + " symbols");
        }
    }

    public static void validateTags(List<Tag> tags) throws IllegalArgumentException {
        for (Tag tag : tags) {
            validateTag(tag);
        }
    }
}
