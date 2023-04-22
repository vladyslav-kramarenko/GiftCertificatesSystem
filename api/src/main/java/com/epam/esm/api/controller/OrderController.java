package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.NestedOrderAssembler;
import com.epam.esm.api.assembler.OrderAssembler;
import com.epam.esm.api.dto.OrderDTO;
import com.epam.esm.core.dto.OrderRequest;
import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.api.util.Constants.*;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderAssembler orderAssembler;

    @Autowired
    public OrderController(OrderService orderService, OrderAssembler orderAssembler) {
        this.orderService = orderService;
        this.orderAssembler = orderAssembler;
    }

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> getOrders(
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams) {
        try {
            List<UserOrder> orders = orderService.getOrders(page, size, sortParams);
            if (orders.size() > 0) {
                CollectionModel<OrderDTO> orderCollection = orderAssembler.toCollectionModel(orders, page, size, sortParams);
                return ResponseEntity.ok(orderCollection);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found", "40401"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            Optional<UserOrder> order = orderService.getOrderById(id);
            if (order.isPresent()) return ResponseEntity.ok(orderAssembler.toSingleModel(order.get()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found (id = " + id + ")", "40401"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteOrderById(@PathVariable Long id) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Deleting Orders is not allowed", "40001"));
    }


    @PostMapping("")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest orderRequest) {
        try {
            UserOrder newOrder = orderService.createOrder(orderRequest);
            return new ResponseEntity<>(newOrder, HttpStatus.CREATED);
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "40001"));
        }
    }
}
