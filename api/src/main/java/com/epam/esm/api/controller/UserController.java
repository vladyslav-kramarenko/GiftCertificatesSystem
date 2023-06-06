package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.NestedUserAssembler;
import com.epam.esm.api.assembler.UserAssembler;
import com.epam.esm.api.assembler.order.OrderAssembler;
import com.epam.esm.api.dto.NestedUserDTO;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.OrderService;
import com.epam.esm.core.service.UserService;
import com.epam.esm.core.service.impl.SecurityService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.epam.esm.api.util.Constants.*;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final UserAssembler userAssembler;
    private final NestedUserAssembler nestedUserAssembler;
    private final OrderAssembler userOrderAssembler;
    private final SecurityService securityService;

    @Autowired
    public UserController(
            UserService userService,
            OrderService orderService,
            UserAssembler userAssembler,
            OrderAssembler userOrderAssembler,
            NestedUserAssembler nestedUserAssembler,
            SecurityService securityService) {
        this.userService = Objects.requireNonNull(userService, "UserService must be initialised");
        this.orderService = Objects.requireNonNull(orderService, "OrderService must be initialised");
        this.userOrderAssembler = Objects.requireNonNull(userOrderAssembler, "OrderAssembler must be initialised");
        this.userAssembler = Objects.requireNonNull(userAssembler, "UserAssembler must be initialised");
        this.nestedUserAssembler = Objects.requireNonNull(nestedUserAssembler, "NestedUserAssembler must be initialised");
        this.securityService = Objects.requireNonNull(securityService, "SecurityService must be initialised");
    }

        @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) @Min(value = 0, message = "Page number can't be negative") int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) @Min(value = 0, message = "Page size can't be negative") int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams) throws ServiceException {
        List<User> users = userService.getUsers(page, size, sortParams);
        if (users.size() > 0) {
            CollectionModel<NestedUserDTO> userCollection = nestedUserAssembler.toCollectionModel(users, page, size, sortParams);
            return ResponseEntity.ok(userCollection);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Requested resource not found", ERROR_CODE_40401));
    }


    @PostMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> addUser(@RequestBody @NotNull User user) throws ServiceException {
        return ResponseEntity.ok(userAssembler.toModel(userService.createUser(user)));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("UDeleting Users is not allowed");
    }

    @PutMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUserById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Updating Users is not allowed");
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserById(
            @PathVariable @Min(0) Long id,
            Authentication authentication
    ) throws ServiceException {
        if (securityService.allowedByIdOrManagerOrAdmin(id, authentication)) {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) return ResponseEntity.ok(userAssembler.toModel(user.get()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found (id = " + id + ")", ERROR_CODE_40401));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<?> getOrdersByUserId(
            Authentication authentication,
            @PathVariable @Min(0) Long userId) {
        if (securityService.allowedByIdOrManagerOrAdmin(userId, authentication)) {
            List<UserOrder> orders = orderService.getOrdersByUserId(userId);
            if (orders.size() > 0) {
                return ResponseEntity.ok(userOrderAssembler.toCollectionModel(orders));
            } else return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
