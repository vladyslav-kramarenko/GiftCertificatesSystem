package com.epam.esm.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
public class UserDTO extends RepresentationModel<UserDTO> {
    private Long id;
    private String firstName;
    private String lastName;
    private List<OrderDTO> orders;
}
