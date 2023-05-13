package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.NestedUserAssembler;
import com.epam.esm.api.assembler.UserAssembler;
import com.epam.esm.api.assembler.order.OrderAssembler;
import com.epam.esm.api.dto.NestedUserDTO;
import com.epam.esm.core.dto.CustomUserDetails;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.OrderService;
import com.epam.esm.core.service.UserService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    @Autowired
    public UserController(
            UserService userService,
            OrderService orderService,
            UserAssembler userAssembler,
            OrderAssembler userOrderAssembler,
            NestedUserAssembler nestedUserAssembler
    ) {
        this.userService = Objects.requireNonNull(userService, "UserService must be initialised");
        this.orderService = Objects.requireNonNull(orderService, "OrderService must be initialised");
        this.userOrderAssembler = Objects.requireNonNull(userOrderAssembler, "OrderAssembler must be initialised");
        this.userAssembler = Objects.requireNonNull(userAssembler, "UserAssembler must be initialised");
        this.nestedUserAssembler = Objects.requireNonNull(nestedUserAssembler, "NestedUserAssembler must be initialised");
    }

    //    @PreAuthorize("hasRole('ADMIN') || hasRole('MANAGER')")
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
                .body(new ErrorResponse("Requested resource not found", "40401"));
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getUserById(
            @PathVariable @Min(0) Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) throws ServiceException {
        if (isUserAllowedToGenInfo(id, userDetails)) {
            Optional<User> user = userService.getUserById(id);
            if (user.isPresent()) return ResponseEntity.ok(userAssembler.toModel(user.get()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found (id = " + id + ")", "40401"));

        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
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

    @GetMapping("/{userId}/orders")
    public ResponseEntity<?> getOrdersByUserId(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable @Min(0) Long userId) {
        if (isUserAllowedToGenInfo(userId, userDetails)) {
            List<UserOrder> orders = orderService.getOrdersByUserId(userId);
            if (orders.size() > 0) {
                return ResponseEntity.ok(userOrderAssembler.toCollectionModel(orders));
            } else return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    private boolean isUserAllowedToGenInfo(Long userId, UserDetails userDetails) {
        Long userIdFromToken = null;
        if (userDetails == null) return false;
        boolean isRoleAdminOrManager = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_MANAGER") || role.equals("ROLE_ADMIN"));
        if (isRoleAdminOrManager) return true;
        if (userDetails instanceof CustomUserDetails) {
            userIdFromToken = ((CustomUserDetails) userDetails).getId();
        } else {
            Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
            if (optionalUser.isPresent()) {
                userIdFromToken = optionalUser.get().getId();
            }
        }
        return userId.equals(userIdFromToken);
    }

//    @Profile("Prod")
//    @GetMapping("/{userId}/orders")
//    public ResponseEntity<?> getOrdersByUserId_auth0(
//            @AuthenticationPrincipal Jwt jwt,
//            @PathVariable @Min(0) Long userId) {
//        String email = jwt.getClaim("https://gift-certificates-system-api/email");
//        if (isUserLooksForHisInfo(userId, email)) {
//            List<UserOrder> orders = orderService.getOrdersByUserId(userId);
//            if (orders.size() > 0) {
//                return ResponseEntity.ok(userOrderAssembler.toCollectionModel(orders));
//            } else return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//    }


}
