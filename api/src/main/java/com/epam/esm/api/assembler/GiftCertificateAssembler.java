package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.GiftCertificateController;
import com.epam.esm.api.dto.GiftCertificateDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.esm.api.util.LinksUtils.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftCertificateAssembler implements RepresentationModelAssembler<GiftCertificate, GiftCertificateDTO> {
    private final TagAssembler tagAssembler;
    private final NestedTagAssembler nestedTagAssembler;

    @Autowired
    public GiftCertificateAssembler(TagAssembler tagAssembler, NestedTagAssembler nestedTagAssembler) {
        this.tagAssembler = tagAssembler;
        this.nestedTagAssembler = nestedTagAssembler;
    }

    @Override
    public GiftCertificateDTO toModel(GiftCertificate giftCertificate) {
        GiftCertificateDTO dto = getGiftCertificateDTO(giftCertificate);
        dto.setTags(giftCertificate.getTags().stream().map(nestedTagAssembler::toModel).toList());
        return dto;
    }

    public GiftCertificateDTO toSingleModel(GiftCertificate giftCertificate) {
        GiftCertificateDTO dto = getGiftCertificateDTO(giftCertificate);
        dto.setTags(giftCertificate.getTags().stream().map(tagAssembler::toModel).toList());
        dto.add(new CustomLink(linkTo(methodOn(GiftCertificateController.class).updateGiftCertificate(giftCertificate.getId(), giftCertificate))
                .toUriComponentsBuilder().toUriString(), "updateGiftCertificate", "PUT"));
        dto.add(new CustomLink(linkTo(methodOn(GiftCertificateController.class).deleteGiftCertificate(giftCertificate.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteGiftCertificate", "DELETE"));
        dto.add(getCreateGiftCertificateLink());
        return dto;
    }

    private GiftCertificateDTO getGiftCertificateDTO(GiftCertificate giftCertificate) {

        GiftCertificateDTO dto = new GiftCertificateDTO();
        dto.setId(giftCertificate.getId());
        dto.setName(giftCertificate.getName());
        dto.setDescription(giftCertificate.getDescription());
        dto.setPrice(giftCertificate.getPrice());
        dto.setDuration(giftCertificate.getDuration());
        dto.setCreateDate(giftCertificate.getCreateDate());
        dto.setLastUpdateDate(giftCertificate.getLastUpdateDate());

        dto.add(getGiftCertificateSelfLink(giftCertificate.getId()));
        return dto;
    }

    public CollectionModel<GiftCertificateDTO> toCollectionModel(
            List<GiftCertificate> orders,
            String search,
            String[] tags,
            int page,
            int size,
            String[] sortParams
    ) {
        List<GiftCertificateDTO> giftCertificateDTOs = orders.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<GiftCertificateDTO> collection = CollectionModel.of(giftCertificateDTOs);
        addOrderNavigationLinks(collection, orders, search, tags, page, size, sortParams);
        return collection;
    }
}
