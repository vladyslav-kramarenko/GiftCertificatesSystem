package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.OrderController;
import com.epam.esm.api.controller.UserController;
import com.epam.esm.api.dto.GiftCertificateDTO;
import com.epam.esm.api.dto.OrderDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.UserOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderAssembler implements RepresentationModelAssembler<UserOrder, OrderDTO> {

    private final GiftCertificateAssembler giftCertificateAssembler;

    @Autowired
    public OrderAssembler(GiftCertificateAssembler giftCertificateAssembler) {
        this.giftCertificateAssembler = giftCertificateAssembler;
    }

    @Override
    public OrderDTO toModel(UserOrder order) {
        OrderDTO OrderDTO = new OrderDTO();
        OrderDTO.setId(order.getId());
        OrderDTO.setSum(order.getSum());
        OrderDTO.setCreateDate(order.getCreateDate());
        OrderDTO.setLastUpdateDate(order.getLastUpdateDate());

        List<GiftCertificateDTO> giftCertificateDTOs = order.getOrderGiftCertificates().stream()
                .map(orderGiftCertificate -> giftCertificateAssembler.toModel(orderGiftCertificate.getGiftCertificate()))
                .toList();

        OrderDTO.setGiftCertificates(giftCertificateDTOs);

//        OrderDTO.add(
//                linkTo(methodOn(UserController.class)
//                .getUserOrderById(order.getUser().getId(), order.getId())).withSelfRel());

        OrderDTO.add(new CustomLink(linkTo(methodOn(OrderController.class).getOrderById(order.getId()))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        OrderDTO.add(new CustomLink(linkTo(methodOn(OrderController.class).deleteOrderById(order.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteOrder", "DELETE"));
        OrderDTO.add(new CustomLink(linkTo(methodOn(UserController.class).createOrder(order.getUser().getId(),null))
                .toUriComponentsBuilder().toUriString(), "createOrder", "POST"));

        return OrderDTO;
    }

    public CollectionModel<OrderDTO> toCollectionModel(List<UserOrder> orders, int page, int size, String[] sortParams) {
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<OrderDTO> orderCollection = CollectionModel.of(orderDTOs);

        orderCollection.add(linkTo(methodOn(OrderController.class).getOrders(page, size, sortParams)).withSelfRel());
        orderCollection.add(linkTo(methodOn(OrderController.class).getOrders(0, size, sortParams)).withRel("first"));
        if (page > 0) {
            orderCollection.add(linkTo(methodOn(OrderController.class).getOrders(page - 1, size, sortParams)).withRel("previous"));
        }
        if (!orders.isEmpty()) {
            orderCollection.add(linkTo(methodOn(OrderController.class).getOrders(page + 1, size, sortParams)).withRel("next"));
        }

        return orderCollection;
    }
}
