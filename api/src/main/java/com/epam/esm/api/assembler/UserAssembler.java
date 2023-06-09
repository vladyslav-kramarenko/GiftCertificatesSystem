package com.epam.esm.api.assembler;

import com.epam.esm.api.assembler.order.NestedOrderAssembler;
import com.epam.esm.api.controller.UserController;
import com.epam.esm.api.dto.order.NestedOrderDTO;
import com.epam.esm.api.dto.UserDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.exception.ServiceException;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.epam.esm.api.util.Constants.*;
import static com.epam.esm.api.util.LinksUtils.addUserNavigationLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.web.servlet.support.WebContentGenerator.METHOD_POST;

@Component
public class UserAssembler implements RepresentationModelAssembler<User, UserDTO> {
    private final NestedOrderAssembler nestedUserOrderAssembler;
    @Autowired
    public UserAssembler(NestedOrderAssembler nestedUserOrderAssembler) {
        this.nestedUserOrderAssembler = Objects.requireNonNull(nestedUserOrderAssembler, "NestedOrderAssembler must be initialised");
    }
    @SneakyThrows(ServiceException.class)
    @Override
    public UserDTO toModel(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setEmail(user.getEmail());
        List<NestedOrderDTO> orders = user.getOrders().stream()
                .map(nestedUserOrderAssembler::toModel)
                .toList();
        userDTO.setOrders(orders);
        userDTO.add(new CustomLink(linkTo(methodOn(UserController.class).getUserById(user.getId(),null))
                .toUriComponentsBuilder().toUriString(), SELF, METHOD_GET));
        userDTO.add(new CustomLink(linkTo(methodOn(UserController.class).deleteUserById(user.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteUser", METHOD_DELETE));
        userDTO.add(getCreateUserLink());
        return userDTO;
    }
    public CollectionModel<UserDTO> toCollectionModel(List<User> users, int page, int size, String[] sortParams) throws ServiceException {
        List<UserDTO> userDTOs = users.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<UserDTO> userCollection = CollectionModel.of(userDTOs);
        addUserNavigationLinks(userCollection, users, page, size, sortParams);
        return userCollection;
    }
    private CustomLink getCreateUserLink() throws ServiceException {
        return new CustomLink(linkTo(methodOn(UserController.class).addUser(null))
                .toUriComponentsBuilder().toUriString(), "CreateUser", METHOD_POST);
    }
}
