package com.epam.esm.core.service;

import com.epam.esm.core.dto.GiftCertificateOrder;
import com.epam.esm.core.dto.OrderRequest;
import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.repository.*;
import com.epam.esm.core.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class OrderServiceImplTest {
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderGiftCertificateRepository orderGiftCertificateRepository;
    @Mock
    private GiftCertificateRepository giftCertificateRepository;

    private User user;
    private GiftCertificate giftCertificate;
    private UserOrder userOrder;
    private List<GiftCertificateOrder> giftCertificateOrders;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setId(1L);

        giftCertificate = new GiftCertificate();
        giftCertificate.setId(1L);
        giftCertificate.setName("Test Certificate");
        giftCertificate.setDescription("Test Description");
        giftCertificate.setPrice(BigDecimal.valueOf(100));
        giftCertificate.setDuration(30);

        userOrder = new UserOrder();
        userOrder.setId(1L);
        userOrder.setSum(BigDecimal.valueOf(100));
        userOrder.setUser(user);

        giftCertificateOrders = List.of(new GiftCertificateOrder(1L, 1));
    }

    @Test
    public void getOrderById_success() {
        when(orderRepository.findById(1L)).thenReturn(Optional.of(userOrder));

        Optional<UserOrder> result = orderService.getOrderById(1L);

        assertEquals(userOrder, result.orElse(null));
    }

    @Test
    public void getOrdersByUserId_success() {
        when(orderRepository.findByUserId(1L)).thenReturn(List.of(userOrder));

        List<UserOrder> result = orderService.getOrdersByUserId(1L);

        assertEquals(List.of(userOrder), result);
    }

    @Test
    public void getOrders_success() {
        Sort sort = Sort.by("id").ascending();
        int page = 0;
        int size = 10;
        PageRequest pageable = PageRequest.of(page, size, sort);
        PageImpl<UserOrder> userOrderPage = new PageImpl<>(List.of(userOrder));

        when(orderRepository.findAll(pageable)).thenReturn(userOrderPage);

        List<UserOrder> result = orderService.getOrders(page, size, new String[]{"id", "asc"});
        assertEquals(List.of(userOrder), result);
    }

    @Test
    public void createOrder_success() {
        OrderRequest orderRequest = new OrderRequest(1L, giftCertificateOrders);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(giftCertificateRepository.findById(1L)).thenReturn(Optional.of(giftCertificate));
        when(orderRepository.save(any(UserOrder.class))).thenReturn(userOrder);
        when(orderGiftCertificateRepository.saveAll(any(List.class))).thenReturn(null);

        UserOrder result = orderService.createOrder(orderRequest);

        assertEquals(userOrder, result);
    }
}