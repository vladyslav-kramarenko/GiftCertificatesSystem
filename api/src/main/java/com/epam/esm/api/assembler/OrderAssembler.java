package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.OrderController;
import com.epam.esm.api.dto.NestedGiftCertificateDTO;
import com.epam.esm.api.dto.OrderDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.UserOrder;
import com.epam.esm.core.exception.ServiceException;
import jakarta.validation.constraints.NotNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.epam.esm.api.util.LinksUtils.addOrderNavigationLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderAssembler implements RepresentationModelAssembler<UserOrder, OrderDTO> {
    private final NestedGiftCertificateAssembler nestedGiftCertificateAssembler;
    private final NestedUserAssembler nestedUserAssembler;

    @Autowired
    public OrderAssembler(
            NestedGiftCertificateAssembler nestedGiftCertificateAssembler,
            NestedUserAssembler nestedUserAssembler
    ) {
        this.nestedUserAssembler = Objects.requireNonNull(nestedUserAssembler, "NestedUserAssembler must be initialised");
        this.nestedGiftCertificateAssembler = Objects.requireNonNull(nestedGiftCertificateAssembler, "NestedGiftCertificateAssembler must be initialised");
    }

    @SneakyThrows(ServiceException.class)
    @Override
    @NotNull
    public OrderDTO toModel(UserOrder order) {
        OrderDTO orderDTO = getOrderDTO(order);
        orderDTO.setUser(nestedUserAssembler.toNestedModel(order.getUser()));
        List<NestedGiftCertificateDTO> giftCertificateDTOs = order.getOrderGiftCertificates().stream()
                .map(orderGiftCertificate -> nestedGiftCertificateAssembler.toModel(orderGiftCertificate.getGiftCertificate()))
                .toList();
        orderDTO.setGiftCertificates(giftCertificateDTOs);
        return orderDTO;
    }

    public OrderDTO toSingleModel(UserOrder order) throws ServiceException {
        OrderDTO orderDTO = getOrderDTO(order);
        orderDTO.setUser(nestedUserAssembler.toModel(order.getUser()));
        List<NestedGiftCertificateDTO> giftCertificateDTOs = order.getOrderGiftCertificates().stream()
                .map(orderGiftCertificate -> nestedGiftCertificateAssembler.toSingleModel(orderGiftCertificate.getGiftCertificate()))
                .toList();
        orderDTO.setGiftCertificates(giftCertificateDTOs);

        orderDTO.add(new CustomLink(linkTo(methodOn(OrderController.class).deleteOrderById(order.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteOrder", "DELETE"));
        return orderDTO;
    }

    private OrderDTO getOrderDTO(UserOrder order) throws ServiceException {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(order.getId());
        orderDTO.setSum(order.getSum());
        orderDTO.setCreateDate(order.getCreateDate());
        orderDTO.setLastUpdateDate(order.getLastUpdateDate());

        orderDTO.add(new CustomLink(linkTo(methodOn(OrderController.class).getOrderById(order.getId()))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        return orderDTO;
    }

    public CollectionModel<OrderDTO> toCollectionModel(List<UserOrder> orders, int page, int size, String[] sortParams) throws ServiceException {
        List<OrderDTO> orderDTOs = orders.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<OrderDTO> orderCollection = CollectionModel.of(orderDTOs);
        addOrderNavigationLinks(orderCollection,orders, page, size, sortParams);
        return orderCollection;
    }
}
