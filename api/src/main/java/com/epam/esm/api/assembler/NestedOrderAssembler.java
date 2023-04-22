package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.OrderController;
import com.epam.esm.api.dto.NestedGiftCertificateDTO;
import com.epam.esm.api.dto.NestedOrderDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.UserOrder;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.esm.api.util.LinksUtils.addOrderNavigationLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NestedOrderAssembler implements RepresentationModelAssembler<UserOrder, NestedOrderDTO> {
    private final NestedGiftCertificateAssembler nestedGiftCertificateAssembler;

    @Autowired
    public NestedOrderAssembler(NestedGiftCertificateAssembler nestedGiftCertificateAssembler) {
        this.nestedGiftCertificateAssembler = nestedGiftCertificateAssembler;
    }

    @Override
    @NotNull
    public NestedOrderDTO toModel(UserOrder order) {
        NestedOrderDTO orderDTO = new NestedOrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setSum(order.getSum());
        orderDTO.setCreateDate(order.getCreateDate());
        orderDTO.setLastUpdateDate(order.getLastUpdateDate());

        List<NestedGiftCertificateDTO> giftCertificateDTOs = order.getOrderGiftCertificates().stream()
                .map(orderGiftCertificate -> nestedGiftCertificateAssembler.toModel(orderGiftCertificate.getGiftCertificate()))
                .toList();

        orderDTO.setGiftCertificates(giftCertificateDTOs);

        orderDTO.add(new CustomLink(linkTo(methodOn(OrderController.class).getOrderById(order.getId()))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        return orderDTO;
    }

    public CollectionModel<NestedOrderDTO> toCollectionModel(List<UserOrder> orders, int page, int size, String[] sortParams) {
        List<NestedOrderDTO> orderDTOs = orders.stream()
                .map(this::toModel)
                .toList();
        CollectionModel<NestedOrderDTO> orderCollection = CollectionModel.of(orderDTOs);
        addOrderNavigationLinks(orderCollection, orders, page, size, sortParams);
        return orderCollection;
    }
}