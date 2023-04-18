package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.*;
import com.epam.esm.core.exception.ServiceException;
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
import static com.epam.esm.core.util.Utilities.validateId;

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
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderGiftCertificateRepository = orderGiftCertificateRepository;
        this.giftCertificateRepository = giftCertificateRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<UserOrder> getOrderById(Long id) throws ServiceException {
        validateId(id);
        try {
            Optional<UserOrder> orderOpt = orderRepository.findById(id);
            if (orderOpt.isPresent()) {
                UserOrder order = orderOpt.get();
                Hibernate.initialize(order.getOrderGiftCertificates());
                return Optional.of(order);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while get order with id = " + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOrder(Long id) throws ServiceException {
        validateId(id);
        try {
            orderRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while deleting order with id = " + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UserOrder> getOrders(int page, int size, String[] sortParams) throws ServiceException {
        Optional<Sort> sort = createSort(sortParams, ALLOWED_ORDER_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        Pageable pageable = PageRequest.of(page, size, sort.orElse(Sort.by("id").ascending()));

        try {
            return orderRepository.findAll(pageable).toList();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while getting all orders");
        }
    }

    @Transactional
    @Override
    public UserOrder createOrder(Long userId, List<OrderRequest> orderRequests) throws ServiceException {
        validateId(userId);
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User with id = " + userId + " not found"));

            Map<GiftCertificate, Integer> giftCertificatesWithQuantity = getCertificateMap(orderRequests);

            BigDecimal orderSum = calculateOrderSum(giftCertificatesWithQuantity);

            UserOrder savedUserOrder = orderRepository.save(new UserOrder(orderSum, user));

            addGiftCertificatesToOrder(savedUserOrder, giftCertificatesWithQuantity);

            return savedUserOrder;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }

    private Map<GiftCertificate, Integer> getCertificateMap(List<OrderRequest> orderRequests) {
        Map<GiftCertificate, Integer> giftCertificatesWithQuantity = new HashMap<>();

        for (OrderRequest orderRequest : orderRequests) {
            Optional<GiftCertificate> gc = giftCertificateRepository.findById(orderRequest.giftCertificateID());
            if (gc.isPresent()) {
                giftCertificatesWithQuantity.put(gc.get(), orderRequest.quantity());
            } else {
                throw new IllegalArgumentException("Gift Certificate with id = " + orderRequest.giftCertificateID() + " not found");
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


    private BigDecimal calculateOrderSum(Map<GiftCertificate, Integer> giftCertificatesWithQuantity) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<GiftCertificate, Integer> entry : giftCertificatesWithQuantity.entrySet()) {
            GiftCertificate giftCertificate = entry.getKey();
            int count = entry.getValue();
            sum = sum.add(giftCertificate.getPrice().multiply(new BigDecimal(count)));
        }
        return sum;
    }
}
