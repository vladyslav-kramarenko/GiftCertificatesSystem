package com.epam.esm.util;

import com.epam.esm.model.Tag;

public class testUtils {
    public static String generateStringBySize(int size) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < size; i++) {
            text.append(i);
        }
        return text.toString();
    }

    public static Tag generateTag(long id) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName("Tag " + id);
        return tag;
    }
}
