package com.epam.esm.api.dto;

import com.epam.esm.api.dto.order.NestedOrderDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO extends NestedUserDTO {
    private List<NestedOrderDTO> orders;
}
