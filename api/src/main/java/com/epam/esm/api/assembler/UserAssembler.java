package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.UserController;
import com.epam.esm.api.dto.UserDTO;
import com.epam.esm.api.dto.OrderDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAssembler implements RepresentationModelAssembler<User, UserDTO> {
    private final OrderAssembler userOrderAssembler;

    @Autowired
    public UserAssembler(OrderAssembler userOrderAssembler) {
        this.userOrderAssembler = userOrderAssembler;
    }

    @Override
    public UserDTO toModel(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());


        List<OrderDTO> orders = user.getOrders().stream()
                .map(userOrderAssembler::toModel)
                .toList();

        userDTO.setOrders(orders);

//        userDTO.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());
//        userDTO.add(linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel());

        userDTO.add(new CustomLink(linkTo(methodOn(UserController.class).getUserById(user.getId()))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        userDTO.add(new CustomLink(linkTo(methodOn(UserController.class).deleteUserById(user.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteUser", "DELETE"));
        userDTO.add(new CustomLink(linkTo(methodOn(UserController.class).addUser(null)).toUriComponentsBuilder()
                .toUriString(), "createUser", "POST"));


        return userDTO;
    }

    public CollectionModel<UserDTO> toCollectionModel(List<User> users, int page, int size, String[] sortParams) {
        List<UserDTO> userDTOs = users.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<UserDTO> tagCollection = CollectionModel.of(userDTOs);

        tagCollection.add(linkTo(methodOn(UserController.class).getUsers(page, size, sortParams)).withSelfRel());
        tagCollection.add(linkTo(methodOn(UserController.class).getUsers(0, size, sortParams)).withRel("first"));
        if (page > 0) {
            tagCollection.add(linkTo(methodOn(UserController.class).getUsers(page - 1, size, sortParams)).withRel("previous"));
        }
        if (!users.isEmpty()) {
            tagCollection.add(linkTo(methodOn(UserController.class).getUsers(page + 1, size, sortParams)).withRel("next"));
        }
        return tagCollection;
    }
}
