package com.epam.esm.api.assembler.order;

import com.epam.esm.api.dto.order.BaseOrderDTO;
import com.epam.esm.core.entity.UserOrder;

public class OrderMapper {
    public static <T extends BaseOrderDTO> T mapGiftCertificateToDto(UserOrder order, T dto) {
        dto.setId(order.getId());
        dto.setSum(order.getSum());
        dto.setCreateDate(order.getCreateDate());
        dto.setLastUpdateDate(order.getLastUpdateDate());
        return dto;
    }
}
