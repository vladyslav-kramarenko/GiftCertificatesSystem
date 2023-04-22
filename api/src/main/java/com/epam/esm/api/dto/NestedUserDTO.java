package com.epam.esm.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class NestedUserDTO extends RepresentationModel<NestedUserDTO> {
    private Long id;
    private String firstName;
    private String lastName;
}
