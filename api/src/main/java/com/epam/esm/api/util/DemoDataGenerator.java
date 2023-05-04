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
import org.hibernate.PersistentObjectException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DemoDataGenerator {
    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final GiftCertificateRepository giftCertificateRepository;
    private final OrderService orderService;
    private static final Logger logger = LoggerFactory.getLogger(DemoDataGenerator.class);


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
        List<Tag> savedTags = null;
        List<GiftCertificate> savedGiftCertificates = null;
        if (userCount > 0) {
            Set<User> users = generateUsers(userCount);
            List<User> createdUsers = userRepository.saveAll(users);
            result.append(createdUsers.size()).append(" users, ");
            logger.info(createdUsers.size()+" users");
        }
        if (tagsCount > 0) {
            savedTags = tagRepository.findAll();
            Set<Tag> tags = generateTags(tagsCount, savedTags);
            List<Tag> createdTags = saveEntitiesBatch(tagRepository, new ArrayList<>(tags), 50);
            savedTags.addAll(createdTags);
            result.append(createdTags.size()).append(" tags, ");
            logger.info(createdTags.size()+" tags");
        }
        if (giftCertificateCount > 0) {
            savedGiftCertificates = giftCertificateRepository.findAll();
            if (savedTags == null) savedTags = tagRepository.findAll();
            Set<GiftCertificate> giftCertificates = generateGiftCertificate(savedTags, giftCertificateCount, savedGiftCertificates);
            List<GiftCertificate> createdGiftCertificates = saveEntitiesBatch(giftCertificateRepository, new ArrayList<>(giftCertificates), 50);
            savedGiftCertificates.addAll(createdGiftCertificates);
            result.append(createdGiftCertificates.size()).append(" gift certificates, ");
            logger.info(createdGiftCertificates.size()+" gift certificates");
        }

        if (orderCount > 0) {
            if (savedGiftCertificates == null) savedGiftCertificates = giftCertificateRepository.findAll();
            List<User> savedUsers = userRepository.findAll();
            Set<UserOrder> orders = generateOrders(savedUsers, savedGiftCertificates, orderCount);

            result.append(orders.size()).append(" orders");
            logger.info(orders.size()+" orders");
        }
        return result.toString();
    }

    private <T> List<T> saveEntitiesBatch(JpaRepository<T, ?> repository, List<T> entities, int batchSize) {
        List<T> savedEntities = new ArrayList<>();
        int start = 0;
        while (start < entities.size()) {
            int end = Math.min(start + batchSize, entities.size());
            List<T> batch = entities.subList(start, end);
            try {
                savedEntities.addAll(repository.saveAll(batch));
            } catch (Exception e) {
                logger.error("Error saving batch: {}{}", batch, e.getMessage());
                // Save entities individually when batch insert fails due to data integrity violation
                for (T entity : batch) {
                    try {
                        repository.save(entity);
                        savedEntities.add(entity);
                    } catch (InvalidDataAccessApiUsageException | PersistentObjectException |
                             DataIntegrityViolationException ex) {
                        logger.error("Error saving entity: {}{}", entity, ex.getMessage());
                    }
                }
            }
            start = end;
        }
        return savedEntities;
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

    private Set<Tag> generateTags(int tagsCount, List<Tag> tagsInDb) {
        Set<Tag> tags = new HashSet<>();
        Set<String> names = tagsInDb.stream().map(Tag::getName).collect(Collectors.toSet());
        for (int i = 0; i < tagsCount; i++) {
            String name = generateTagName(names);
            if (!name.isEmpty()) {
                Tag tag = new Tag();
                tag.setName(name);
                tags.add(tag);
            }
        }
        return tags;
    }

    private Set<GiftCertificate> generateGiftCertificate(List<Tag> tags, int giftCertificateCount, List<GiftCertificate> certificatesInDb) {
        Set<GiftCertificate> giftCertificates = new HashSet<>();
        Set<String> names = certificatesInDb.stream().map(GiftCertificate::getName).collect(Collectors.toSet());
        for (int i = 0; i < giftCertificateCount; i++) {
            GiftCertificate giftCertificate = generateGiftCertificate(names);
            if (giftCertificate != null) {
                giftCertificate.setTags(chooseSomeTags(tags));
                giftCertificates.add(giftCertificate);
            }
        }
        return giftCertificates;
    }

    private GiftCertificate generateGiftCertificate(Set<String> names) {
        GiftCertificate giftCertificate = new GiftCertificate();
        String name = generateCertificateName(names);
        if (name.isEmpty()) return null;
        giftCertificate.setName(name);
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
        List<Function<Faker, String>> nameGenerators = Arrays.asList(
                f -> f.commerce().productName(),
                f -> f.commerce().productName() + " - " + f.commerce().color(),
                f -> f.ancient().god() + " - " + f.commerce().color(),
                f -> f.ancient().hero() + " - " + f.color(),
                f -> f.funnyName().name(),
                f -> f.color().name() + " " + f.commerce().material()
        );
        return generateName(nameGenerators, names);
    }

    private String generateName(List<Function<Faker, String>> nameGenerators, Set<String> names) {
        for (Function<Faker, String> nameGenerator : nameGenerators) {
            String name = nameGenerator.apply(faker);
            boolean nameExists = names.stream().anyMatch(existingName -> existingName.equalsIgnoreCase(name));
            if (!nameExists) {
                names.add(name);
                return name;
            }
        }
        return "";
    }

    private String generateTagName(Set<String> names) {
        List<Function<Faker, String>> nameGenerators = Arrays.asList(
                f -> Arrays.stream(Arrays.stream(f.commerce().department().split(","))
                                .findAny().orElse("").split(" & "))
                        .filter(x -> !names.contains(x))
                        .findAny()
                        .orElse(""),
                f -> f.commerce().material(),
                f -> f.commerce().color(),
                f -> f.ancient().god(),
                f -> f.animal().name(),
                f -> f.ancient().hero(),
                f -> f.ancient().titan(),
                f -> f.aviation().aircraft(),
                f -> f.company().industry(),
                f -> f.company().profession(),
                f -> f.aviation().airport(),
                f -> f.name().username()

        );
        return generateName(nameGenerators, names);
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
