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
import java.util.*;

@Component
public class DemoDataGenerator {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final GiftCertificateRepository giftCertificateRepository;

    @Autowired
    public DemoDataGenerator(
            UserRepository userRepository,
            TagRepository tagRepository,
            GiftCertificateRepository giftCertificateRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository must be initialised");
        this.tagRepository = Objects.requireNonNull(tagRepository, "TagRepository must be initialised");
        this.giftCertificateRepository = Objects.requireNonNull(giftCertificateRepository, "GiftCertificateRepository must be initialised");
    }

    private final Faker faker = new Faker();

    public String generateDemoData(int userCount, int tagsCount, int giftCertificateCount) {
        StringBuilder result = new StringBuilder("created ");
        Set<User> users = generateUsers(userCount);
        List<User> createdUsers = userRepository.saveAll(users);
        result.append(createdUsers.size()).append(" users, ");

        Set<Tag> tags = generateTags(tagsCount);
        tags.removeIf(tag -> tagRepository.getByName(tag.getName()).isPresent());
        List<Tag> createdTags = tagRepository.saveAll(tags);
        result.append(createdTags.size()).append(" tags, ");

        List<Tag> savedTags = tagRepository.findAll();
        Set<GiftCertificate> giftCertificates = generateGiftCertificate(savedTags, giftCertificateCount);
        giftCertificates.removeIf(giftCertificate -> giftCertificateRepository.getByName(giftCertificate.getName()).isPresent());

        giftCertificateRepository.saveAll(giftCertificates);
        result.append(giftCertificates.size()).append(" gift certificates");

        return result.toString();
    }

    private Set<User> generateUsers(int userCount) {
        Set<User> users = new HashSet<>();
        for (int i = 0; i < userCount; i++) {
            User user = new User();
            user.setFirstName(faker.name().firstName());
            user.setLastName(faker.name().lastName());
            users.add(user);
        }
        return users;
    }

    private Set<Tag> generateTags(int tagsCount) {
        Set<Tag> tags = new HashSet<>();
        Set<String> names = new HashSet<>();
        for (int i = 0; i < tagsCount; i++) {
            Tag tag = new Tag();
            tag.setName(generateTagName(names));
            tags.add(tag);
        }
        return tags;
    }

    private Set<GiftCertificate> generateGiftCertificate(List<Tag> tags, int giftCertificateCount) {
        Set<GiftCertificate> giftCertificates = new HashSet<>();
        Set<String> names = new HashSet<>();
        for (int i = 0; i < giftCertificateCount; i++) {
            GiftCertificate giftCertificate = generateGiftCertificate(names);
            giftCertificate.setTags(choseSomeTags(tags));
            giftCertificates.add(giftCertificate);
        }
        return giftCertificates;
    }

    private GiftCertificate generateGiftCertificate(Set<String> names) {
        GiftCertificate giftCertificate = new GiftCertificate();
        giftCertificate.setName(generateCertificateName(names));
        String description = faker.lorem().sentence(10);
        giftCertificate.setDescription(description);
        giftCertificate.setPrice(BigDecimal.valueOf(faker.number().randomDouble(2, 10, 1000)));
        giftCertificate.setDuration(faker.number().numberBetween(1, 365));
        return giftCertificate;
    }

    private List<Tag> choseSomeTags(List<Tag> tags) {
        List<Tag> giftCertificateTags = new ArrayList<>();
        int certificateTagsCount = faker.number().numberBetween(1, 5);
        for (int j = 0; j < certificateTagsCount; j++) {
            Tag randomTag = tags.get(faker.number().numberBetween(0, tags.size()));
            if (!giftCertificateTags.contains(randomTag)) {
                giftCertificateTags.add(randomTag);
            }
        }
        System.out.println("Assigned tags: " + giftCertificateTags);
        return giftCertificateTags;
    }


    private String generateCertificateName(Set<String> names) {
        String name;
        boolean test;
        do {
            name = faker.commerce().productName();
            test = names.contains(name);
            if (test) {
                name += " - " + faker.commerce().color();
                test = names.contains(name);
            }
            if (test) {
                name = faker.funnyName().name();
                test = names.contains(name);
            }
        } while (test);
        names.add(name);
        return name;
    }

    private String generateTagName(Set<String> names) {
        String name;
        boolean test;
        do {
            name = faker.commerce().department();
            test = names.contains(name);
        } while (test);
        names.add(name);
        return name;
    }
}
