package com.epam.esm.core.service.impl;

import com.epam.esm.core.dto.GiftCertificateOrder;
import com.epam.esm.core.dto.OrderRequest;
import com.epam.esm.core.entity.*;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.OrderGiftCertificateRepository;
import com.epam.esm.core.repository.OrderRepository;
import com.epam.esm.core.repository.UserRepository;
import com.epam.esm.core.service.OrderService;
import com.epam.esm.core.service.TagService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.epam.esm.core.util.CoreConstants.*;
import static com.epam.esm.core.util.SortUtilities.createSort;
import static com.epam.esm.core.util.Utilities.calculateOrderSum;

/**
 * Implementation of the {@link TagService} interface that provides the business logic for working with tags.
 */
@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderGiftCertificateRepository orderGiftCertificateRepository;
    private final GiftCertificateRepository giftCertificateRepository;

    @Autowired
    public OrderServiceImpl(
            UserRepository userRepository,
            OrderRepository orderRepository,
            OrderGiftCertificateRepository orderGiftCertificateRepository,
            GiftCertificateRepository giftCertificateRepository
    ) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository must be initialised");
        this.orderRepository = Objects.requireNonNull(orderRepository, "OrderRepository must be initialised");
        this.orderGiftCertificateRepository = Objects.requireNonNull(orderGiftCertificateRepository, "OrderGiftCertificateRepository must be initialised");
        this.giftCertificateRepository = Objects.requireNonNull(giftCertificateRepository, "GiftCertificateRepository must be initialised");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserOrder> getOrderById(Long id) {
        Optional<UserOrder> orderOpt = orderRepository.findById(id);
        if (orderOpt.isPresent()) {
            UserOrder order = orderOpt.get();
            Hibernate.initialize(order.getOrderGiftCertificates());
            return Optional.of(order);
        }
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserOrder> getOrders(int page, int size, String[] sortParams) {
        Optional<Sort> sort = createSort(sortParams, ALLOWED_ORDER_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        Pageable pageable = PageRequest.of(page, size, sort.orElse(Sort.by("id").ascending()));
            return orderRepository.findAll(pageable).toList();
    }

    @Transactional
    @Override
    public UserOrder createOrder(OrderRequest orderRequest) {
        User user = userRepository.findById(orderRequest.userId())
                .orElseThrow(() -> new IllegalArgumentException("User with id = " + orderRequest.userId() + " not found"));
        Map<GiftCertificate, Integer> giftCertificatesWithQuantity = getCertificateMap(orderRequest.giftCertificates());
        BigDecimal orderSum = calculateOrderSum(giftCertificatesWithQuantity);
        UserOrder savedUserOrder = orderRepository.save(new UserOrder(orderSum, user));
        addGiftCertificatesToOrder(savedUserOrder, giftCertificatesWithQuantity);
        return savedUserOrder;
    }

    private Map<GiftCertificate, Integer> getCertificateMap(List<GiftCertificateOrder> giftCertificateOrders) {
        Map<GiftCertificate, Integer> giftCertificatesWithQuantity = new HashMap<>();

        for (GiftCertificateOrder giftCertificateOrder : giftCertificateOrders) {
            Optional<GiftCertificate> gc = giftCertificateRepository.findById(giftCertificateOrder.giftCertificateId());
            if (gc.isPresent()) {
                giftCertificatesWithQuantity.put(gc.get(), giftCertificateOrder.quantity());
            } else {
                throw new IllegalArgumentException("Gift Certificate with id = " + giftCertificateOrder.giftCertificateId() + " not found");
            }
        }
        return giftCertificatesWithQuantity;
    }

    private void addGiftCertificatesToOrder(UserOrder savedUserOrder, Map<GiftCertificate, Integer> giftCertificatesWithQuantity) {
        List<OrderGiftCertificate> orderGiftCertificates = new ArrayList<>();
        for (Map.Entry<GiftCertificate, Integer> entry : giftCertificatesWithQuantity.entrySet()) {
            orderGiftCertificates.add(createOrderGiftCertificate(savedUserOrder, entry.getKey(), entry.getValue()));
        }
        savedUserOrder.setOrderGiftCertificates(orderGiftCertificates);
        orderGiftCertificateRepository.saveAll(orderGiftCertificates);
    }

    private OrderGiftCertificate createOrderGiftCertificate(UserOrder savedUserOrder, GiftCertificate giftCertificate, Integer count) {
        OrderGiftCertificate orderGiftCertificate = new OrderGiftCertificate();
        orderGiftCertificate.setId(new OrderGiftCertificateId(savedUserOrder.getId(), giftCertificate.getId()));
        orderGiftCertificate.setOrder(savedUserOrder);
        orderGiftCertificate.setGiftCertificate(giftCertificate);
        orderGiftCertificate.setCount(count);
        return orderGiftCertificate;
    }

    @Override
    public List<UserOrder> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
}
