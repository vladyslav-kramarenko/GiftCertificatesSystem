package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.NestedUserAssembler;
import com.epam.esm.api.assembler.UserAssembler;
import com.epam.esm.api.assembler.OrderAssembler;
import com.epam.esm.api.dto.NestedUserDTO;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.OrderService;
import com.epam.esm.core.service.TagService;
import com.epam.esm.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.api.util.Constants.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final OrderService orderService;
    private final TagService tagService;
    private final UserAssembler userAssembler;
    private final NestedUserAssembler nestedUserAssembler;
    private final OrderAssembler userOrderAssembler;

    @Autowired
    public UserController(
            UserService userService,
            OrderService orderService,
            TagService tagService,
            UserAssembler userAssembler,
            OrderAssembler userOrderAssembler,
            NestedUserAssembler nestedUserAssembler
    ) {
        this.userService = Objects.requireNonNull(userService, "UserService must be initialised");
        this.orderService = Objects.requireNonNull(orderService, "OrderService must be initialised");
        this.tagService = Objects.requireNonNull(tagService, "TagService must be initialised");
        this.userOrderAssembler = Objects.requireNonNull(userOrderAssembler, "OrderAssembler must be initialised");
        this.userAssembler = Objects.requireNonNull(userAssembler, "UserAssembler must be initialised");
        this.nestedUserAssembler = Objects.requireNonNull(nestedUserAssembler, "NestedUserAssembler must be initialised");
    }

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> getUsers(
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams) {
        try {
            List<User> users = userService.getUsers(page, size, sortParams);
            if (users.size() > 0) {
                CollectionModel<NestedUserDTO> userCollection = nestedUserAssembler.toCollectionModel(users, page, size, sortParams);
                return ResponseEntity.ok(userCollection);
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
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) return ResponseEntity.ok(userAssembler.toModel(user.get()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found (id = " + id + ")", "40401"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @PostMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> addUser(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userAssembler.toModel(userService.createUser(user)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Deleting Users is not allowed", "40001"));
    }

    @PutMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> updateUserById(@PathVariable Long id) {
        return ResponseEntity.badRequest().body(new ErrorResponse("Updating Users is not allowed", "40001"));
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<?> getOrdersByUserId(@PathVariable Long userId) {
        try {
            List<UserOrder> orders = orderService.getOrdersByUserId(userId);
            if (orders.size() > 0) {
                return ResponseEntity.ok(userOrderAssembler.toCollectionModel(orders));
            } else return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/tags/most-used")
    public ResponseEntity<Tag> getMostWidelyUsedTagWithHighestCostByUserId(
            @PathVariable Long userId,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size) {
        Optional<Tag> tag = tagService.getMostWidelyUsedTagWithHighestCostByUserId(userId, page, size);
        if (tag.isPresent()) {
            return ResponseEntity.ok(tag.get());
        }
        return ResponseEntity.notFound().build();
    }
}
