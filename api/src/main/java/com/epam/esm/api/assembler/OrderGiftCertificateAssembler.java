package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.GiftCertificateController;
import com.epam.esm.api.dto.OrderGiftCertificateDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.OrderGiftCertificate;
import com.epam.esm.core.exception.ServiceException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

import static com.epam.esm.api.util.LinksUtils.addOrderNavigationLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class OrderGiftCertificateAssembler implements RepresentationModelAssembler<OrderGiftCertificate, OrderGiftCertificateDTO> {


    @Override
    public OrderGiftCertificateDTO toModel(OrderGiftCertificate orderGiftCertificate) {
        return getGiftCertificateDTO(orderGiftCertificate);
    }

    public OrderGiftCertificateDTO toSingleModel(OrderGiftCertificate orderGiftCertificate) throws ServiceException {
        OrderGiftCertificateDTO dto = getGiftCertificateDTO(orderGiftCertificate);
        dto.add(new CustomLink(
                linkTo(methodOn(GiftCertificateController.class)
                        .getGiftCertificateById(orderGiftCertificate.getGiftCertificate().getId())
                ).toUriComponentsBuilder().toUriString(),
                "self",
                "GET"
        ));
        return dto;
    }

    private OrderGiftCertificateDTO getGiftCertificateDTO(OrderGiftCertificate orderGiftCertificate) {
        GiftCertificate giftCertificate = orderGiftCertificate.getGiftCertificate();
        OrderGiftCertificateDTO dto = new OrderGiftCertificateDTO();
        dto.setCount(orderGiftCertificate.getCount());
        dto.setSum(giftCertificate.getPrice().multiply(new BigDecimal(orderGiftCertificate.getCount())));
        dto.setId(giftCertificate.getId());
        dto.setName(giftCertificate.getName());
        dto.setDescription(giftCertificate.getDescription());
        dto.setPrice(giftCertificate.getPrice());
        dto.setDuration(giftCertificate.getDuration());
        dto.setCreateDate(giftCertificate.getCreateDate());
        dto.setLastUpdateDate(giftCertificate.getLastUpdateDate());
        return dto;
    }

    public CollectionModel<OrderGiftCertificateDTO> toCollectionModel(
            List<OrderGiftCertificate> orders,
            String search,
            String[] tags,
            int page,
            int size,
            String[] sortParams
    ) throws ServiceException {
        List<OrderGiftCertificateDTO> giftCertificateDTOs = orders.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<OrderGiftCertificateDTO> collection = CollectionModel.of(giftCertificateDTOs);
        addOrderNavigationLinks(collection, orders, search, tags, page, size, sortParams);
        return collection;
    }
}
