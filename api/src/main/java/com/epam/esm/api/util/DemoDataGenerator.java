package com.epam.esm.api.util;

import com.epam.esm.core.dto.GiftCertificateOrder;
import com.epam.esm.core.dto.OrderRequest;
import com.epam.esm.core.entity.*;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.OrderService;
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
    private final OrderService orderService;

    @Autowired
    public DemoDataGenerator(
            UserRepository userRepository,
            TagRepository tagRepository,
            GiftCertificateRepository giftCertificateRepository,
            OrderService orderService
    ) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository must be initialised");
        this.tagRepository = Objects.requireNonNull(tagRepository, "TagRepository must be initialised");
        this.giftCertificateRepository = Objects.requireNonNull(giftCertificateRepository, "GiftCertificateRepository must be initialised");
        this.orderService = Objects.requireNonNull(orderService, "OrderService must be initialised");
    }

    private final Faker faker = new Faker();

    public String generateDemoData(int userCount, int tagsCount, int giftCertificateCount, int orderCount) throws ServiceException {
        StringBuilder result = new StringBuilder("created ");
        if (userCount > 0) {
            Set<User> users = generateUsers(userCount);
            List<User> createdUsers = userRepository.saveAll(users);
            result.append(createdUsers.size()).append(" users, ");
        }
        if (tagsCount > 0) {
            Set<Tag> tags = generateTags(tagsCount);
            tags.removeIf(tag -> tagRepository.getByName(tag.getName()).isPresent());
            List<Tag> createdTags = tagRepository.saveAll(tags);
            result.append(createdTags.size()).append(" tags, ");
        }
        if (giftCertificateCount > 0) {
            List<Tag> savedTags = tagRepository.findAll();
            Set<GiftCertificate> giftCertificates = generateGiftCertificate(savedTags, giftCertificateCount);
            giftCertificates.removeIf(giftCertificate -> giftCertificateRepository.getByName(giftCertificate.getName()).isPresent());

            giftCertificateRepository.saveAll(giftCertificates);
            result.append(giftCertificates.size()).append(" gift certificates, ");
        }

        if (orderCount > 0) {
            List<GiftCertificate> savedGiftCertificates = giftCertificateRepository.findAll();
            List<User> savedUsers = userRepository.findAll();
            Set<UserOrder> orders = generateOrders(savedUsers, savedGiftCertificates, orderCount);

            result.append(orders.size()).append(" orders");
        }
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
            giftCertificate.setTags(chooseSomeTags(tags));
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

    private List<Tag> chooseSomeTags(List<Tag> tags) {
        List<Tag> giftCertificateTags = new ArrayList<>();
        int certificateTagsCount = faker.number().numberBetween(1, 5);
        for (int j = 0; j < certificateTagsCount; j++) {
            Tag randomTag = tags.get(faker.number().numberBetween(0, tags.size()));
            if (!giftCertificateTags.contains(randomTag)) {
                giftCertificateTags.add(randomTag);
            }
        }
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
            name = Arrays.stream(Arrays.stream(faker.commerce().department().split(","))
                    .findAny().orElse("").split(" & ")).filter(x -> !names.contains(x)).findAny().orElse("");
            test = name.isEmpty() || names.contains(name);
            if (test) {
                name = faker.commerce().material();
                test = name.isEmpty() || names.contains(name);
            }
            if (test) {
                name = faker.commerce().color();
                test = name.isEmpty() || names.contains(name);
            }
            if (test) {
                name = faker.ancient().god();
                test = name.isEmpty() || names.contains(name);
            }
            if (test) {
                name = faker.animal().name();
                test = name.isEmpty() || names.contains(name);
            }
        } while (test);
        names.add(name);
        return name;
    }

    private Set<UserOrder> generateOrders(
            List<User> users, List<GiftCertificate> giftCertificates, int orderCount
    ) throws ServiceException {
        Set<UserOrder> orders = new HashSet<>();
        for (int i = 0; i < orderCount; i++) {
            User user = users.get(faker.number().numberBetween(0, users.size()));

            int certificateCount = faker.number().numberBetween(1, 5);
            List<GiftCertificateOrder> giftCertificateOrders = new ArrayList<>();

            for (int j = 0; j < certificateCount; j++) {
                GiftCertificate randomGiftCertificate = giftCertificates.get(
                        faker.number().numberBetween(0, giftCertificates.size())
                );
                int count = faker.number().numberBetween(1, 10);
                GiftCertificateOrder giftCertificateOrder = new GiftCertificateOrder(randomGiftCertificate.getId(), count);
                giftCertificateOrders.add(giftCertificateOrder);
            }

            OrderRequest orderRequest = new OrderRequest(user.getId(), giftCertificateOrders);
            Optional<UserOrder> createdOrder = orderService.getOrderById(orderService.createOrder(orderRequest).getId());
            createdOrder.ifPresent(orders::add);
        }
        return orders;
    }
}
