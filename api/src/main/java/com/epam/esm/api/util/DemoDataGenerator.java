package com.epam.esm.api.util;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class DemoDataGenerator {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private GiftCertificateRepository giftCertificateRepository;

    private final Faker faker = new Faker();

    public String generateDemoData(int userCount, int tagsCount, int giftCertificateCount) {
        StringBuilder result = new StringBuilder("created ");
        List<User> users = generateUsers(userCount);
        users = userRepository.saveAll(users);
        result.append(users.size()).append(" users, ");

        List<Tag> tags = generateTags(tagsCount);
        tags = tagRepository.saveAll(tags);
        result.append(tags.size()).append(" tags, ");

        List<Tag> savedTags = tagRepository.findAll();
        List<GiftCertificate> giftCertificates = generateGiftCertificate(savedTags, giftCertificateCount);
        giftCertificates = giftCertificateRepository.saveAll(giftCertificates);
        result.append(giftCertificates.size()).append(" gift certificates");
        return result.toString();
    }

    private List<User> generateUsers(int userCount) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            users.add(user);
        }
        return users;
    }

    private List<Tag> generateTags(int tagsCount) {
        List<Tag> tags = new ArrayList<>();
        for (int i = 0; i < tagsCount; i++) {
            Tag tag = new Tag();
            tag.setName(faker.commerce().department());
            tags.add(tag);
        }
        return tags;
    }

    private List<GiftCertificate> generateGiftCertificate(List<Tag> tags, int giftCertificateCount) {
//        Faker faker = new Faker();
        List<GiftCertificate> giftCertificates = new ArrayList<>();
        for (int i = 0; i < giftCertificateCount; i++) {
                GiftCertificate giftCertificate = new GiftCertificate();
                giftCertificate.setName(faker.commerce().productName());
                String description = faker.lorem().sentence(10);
                giftCertificate.setDescription(description);
                giftCertificate.setPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000)));
                giftCertificate.setDuration(faker.number().numberBetween(1, 365));
                // Assign random tags to gift certificate
                List<Tag> giftCertificateTags = new ArrayList<>();
                int certificateTagsCount = faker.number().numberBetween(1, 5);
                for (int j = 0; j < certificateTagsCount; j++) {
                    Tag randomTag = tags.get(faker.number().numberBetween(0, tags.size()));
                    giftCertificateTags.add(randomTag);
                }
                giftCertificate.setTags(giftCertificateTags);
                // Assign random dates
                LocalDateTime lastUpdateDate = faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                giftCertificate.setLastUpdateDate(lastUpdateDate);
                LocalDateTime createDate = faker.date().past(30, TimeUnit.DAYS, Date.from(lastUpdateDate.atZone(ZoneId.systemDefault()).toInstant())).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                giftCertificate.setCreateDate(createDate);
                giftCertificates.add(giftCertificate);
        }
        return giftCertificates;
    }
}
