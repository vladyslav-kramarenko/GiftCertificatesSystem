package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.UserController;
import com.epam.esm.api.dto.NestedUserDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.User;
import com.epam.esm.core.exception.ServiceException;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.esm.api.util.LinksUtils.addUserNavigationLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NestedUserAssembler implements RepresentationModelAssembler<User, NestedUserDTO> {
    @SneakyThrows(ServiceException.class)
    @Override
    public NestedUserDTO toModel(User user) {
        NestedUserDTO userDTO = getNestedUserDTO(user);
        userDTO.add(new CustomLink(linkTo(methodOn(UserController.class).getUserById(user.getId(),null))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        return userDTO;
    }

    public NestedUserDTO toNestedModel(User user) {
        return getNestedUserDTO(user);
    }
    private NestedUserDTO getNestedUserDTO(User user) {
        NestedUserDTO userDTO = new NestedUserDTO();
        userDTO.setId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        return userDTO;
    }
    public CollectionModel<NestedUserDTO> toCollectionModel(List<User> users, int page, int size, String[] sortParams) throws ServiceException {
        List<NestedUserDTO> userDTOs = users.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<NestedUserDTO> userCollection = CollectionModel.of(userDTOs);
        addUserNavigationLinks(userCollection, users, page, size, sortParams);
        return userCollection;
    }
}
