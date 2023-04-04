package com.epam.esm.util;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class testUtils {
    public static String generateStringBySize(int size) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < size; i++) {
            text.append(i);
        }
        return text.toString();
    }

    public static Tag generateTagWithId(long id) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName("Tag " + id);
        return tag;
    }

    public static GiftCertificate generateGiftCertificateWithoutId() {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setName("Test Gift Certificate");
        giftCertificate.setDescription("Test Description");
        giftCertificate.setPrice(BigDecimal.valueOf(10.00));
        giftCertificate.setDuration((30));
        List<Tag> tags = new ArrayList<>();
        tags.add(generateTagWithId(1L));
        tags.add(generateTagWithId(2L));
        giftCertificate.setTags(tags);
        return giftCertificate;
    }
}
