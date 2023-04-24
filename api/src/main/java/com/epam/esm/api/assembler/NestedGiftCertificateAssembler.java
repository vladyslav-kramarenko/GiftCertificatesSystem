package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.GiftCertificateController;
import com.epam.esm.api.dto.NestedGiftCertificateDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.exception.ServiceException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.esm.api.util.LinksUtils.addOrderNavigationLinks;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class NestedGiftCertificateAssembler implements RepresentationModelAssembler<GiftCertificate, NestedGiftCertificateDTO> {


    @Override
    public NestedGiftCertificateDTO toModel(GiftCertificate giftCertificate) {
        return getGiftCertificateDTO(giftCertificate);
    }

    public NestedGiftCertificateDTO toSingleModel(GiftCertificate giftCertificate) {
        NestedGiftCertificateDTO dto = getGiftCertificateDTO(giftCertificate);
        dto.add(new CustomLink(
                linkTo(methodOn(GiftCertificateController.class)
                        .getGiftCertificateById(giftCertificate.getId())
                ).toUriComponentsBuilder().toUriString(),
                "self",
                "GET"
        ));
        return dto;
    }

    private NestedGiftCertificateDTO getGiftCertificateDTO(GiftCertificate giftCertificate) {

        NestedGiftCertificateDTO dto = new NestedGiftCertificateDTO();
        dto.setId(giftCertificate.getId());
        dto.setName(giftCertificate.getName());
        dto.setDescription(giftCertificate.getDescription());
        dto.setPrice(giftCertificate.getPrice());
        dto.setDuration(giftCertificate.getDuration());
        dto.setCreateDate(giftCertificate.getCreateDate());
        dto.setLastUpdateDate(giftCertificate.getLastUpdateDate());

        return dto;
    }

    public CollectionModel<NestedGiftCertificateDTO> toCollectionModel(
            List<GiftCertificate> orders,
            String search,
            String[] tags,
            int page,
            int size,
            String[] sortParams
    ) throws ServiceException {
        List<NestedGiftCertificateDTO> giftCertificateDTOs = orders.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<NestedGiftCertificateDTO> collection = CollectionModel.of(giftCertificateDTOs);
        addOrderNavigationLinks(collection, orders, search, tags, page, size, sortParams);
        return collection;
    }
}
