package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.order.OrderAssembler;
import com.epam.esm.api.dto.order.OrderDTO;
import com.epam.esm.core.dto.OrderRequest;
import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.OrderService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.api.util.Constants.*;

@RestController
@RequestMapping("/orders")
@Validated
public class OrderController {
    private final OrderService orderService;
    private final OrderAssembler orderAssembler;

    @Autowired
    public OrderController(OrderService orderService, OrderAssembler orderAssembler) {
        this.orderService = Objects.requireNonNull(orderService, "OrderService must be initialised");
        this.orderAssembler = Objects.requireNonNull(orderAssembler, "OrderAssembler must be initialised");
    }

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> getOrders(
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE)
            @Min(value = 0, message = "Page number can't be negative")
            int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Min(value = 0, message = "Page size can't be negative")
            int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams) throws ServiceException {
        List<UserOrder> orders = orderService.getOrders(page, size, sortParams);
        if (orders.size() > 0) {
            CollectionModel<OrderDTO> orderCollection = orderAssembler.toCollectionModel(orders, page, size, sortParams);
            return ResponseEntity.ok(orderCollection);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Requested resource not found", "40401"));
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getOrderById(@PathVariable @Min(0) Long id) throws ServiceException {
        Optional<UserOrder> order = orderService.getOrderById(id);
        if (order.isPresent()) return ResponseEntity.ok(orderAssembler.toSingleModel(order.get()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Requested resource not found (id = " + id + ")", "40401"));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteOrderById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Deleting Orders is not allowed");
    }

    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody @Valid @NotNull OrderRequest orderRequest) throws ServiceException {
        UserOrder newOrder = orderService.createOrder(orderRequest);
        return new ResponseEntity<>(orderAssembler.toSingleModel(newOrder), HttpStatus.CREATED);
    }
}
