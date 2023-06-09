package com.epam.esm.api.assembler.order;

import com.epam.esm.api.assembler.giftCertificate.NestedGiftCertificateAssembler;
import com.epam.esm.api.controller.OrderController;
import com.epam.esm.api.dto.giftCertificate.BaseGiftCertificateDTO;
import com.epam.esm.api.dto.order.NestedOrderDTO;
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

import static com.epam.esm.api.assembler.order.OrderMapper.mapGiftCertificateToDto;
import static com.epam.esm.api.util.Constants.METHOD_GET;
import static com.epam.esm.api.util.Constants.SELF;
import static com.epam.esm.api.util.LinksUtils.addOrderNavigationLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NestedOrderAssembler implements RepresentationModelAssembler<UserOrder, NestedOrderDTO> {
    private final NestedGiftCertificateAssembler nestedGiftCertificateAssembler;

    @Autowired
    public NestedOrderAssembler(NestedGiftCertificateAssembler nestedGiftCertificateAssembler) {
        this.nestedGiftCertificateAssembler = Objects.requireNonNull(nestedGiftCertificateAssembler, "NestedGiftCertificateAssembler must be initialised");
    }

    @SneakyThrows(ServiceException.class)
    @Override
    @NotNull
    public NestedOrderDTO toModel(UserOrder order) {
        NestedOrderDTO orderDTO = new NestedOrderDTO();
        mapGiftCertificateToDto(order, orderDTO);

        List<BaseGiftCertificateDTO> giftCertificateDTOs = order.getOrderGiftCertificates().stream()
                .map(orderGiftCertificate -> nestedGiftCertificateAssembler.toModel(orderGiftCertificate.getGiftCertificate()))
                .toList();

        orderDTO.setGiftCertificates(giftCertificateDTOs);

        orderDTO.add(new CustomLink(linkTo(methodOn(OrderController.class).getOrderById(order.getId()))
                .toUriComponentsBuilder().toUriString(), SELF, METHOD_GET));
        return orderDTO;
    }

    public CollectionModel<NestedOrderDTO> toCollectionModel(List<UserOrder> orders, int page, int size, String[] sortParams) throws ServiceException {
        List<NestedOrderDTO> orderDTOs = orders.stream()
                .map(this::toModel)
                .toList();
        CollectionModel<NestedOrderDTO> orderCollection = CollectionModel.of(orderDTOs);
        addOrderNavigationLinks(orderCollection, orders, page, size, sortParams);
        return orderCollection;
    }
}
