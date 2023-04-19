package com.epam.esm.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
@Getter
@Setter
public class TagDTO extends RepresentationModel<TagDTO> {
    Long id;
    String name;
}
