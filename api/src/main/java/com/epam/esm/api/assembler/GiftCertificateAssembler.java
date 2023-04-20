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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class GiftCertificateAssembler implements RepresentationModelAssembler<GiftCertificate, GiftCertificateDTO> {
    private final TagAssembler tagAssembler;

    @Autowired
    public GiftCertificateAssembler(TagAssembler tagAssembler) {
        this.tagAssembler = tagAssembler;
    }

    @Override
    public GiftCertificateDTO toModel(GiftCertificate giftCertificate) {
        GiftCertificateDTO dto = new GiftCertificateDTO();

        dto.setId(giftCertificate.getId());
        dto.setName(giftCertificate.getName());
        dto.setDescription(giftCertificate.getDescription());
        dto.setPrice(giftCertificate.getPrice());
        dto.setDuration(giftCertificate.getDuration());
        dto.setCreateDate(giftCertificate.getCreateDate());
        dto.setLastUpdateDate(giftCertificate.getLastUpdateDate());
        dto.setTags(giftCertificate.getTags().stream().map(tagAssembler::toModel).toList());

        dto.add(new CustomLink(linkTo(methodOn(GiftCertificateController.class).getGiftCertificateById(giftCertificate.getId()))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        dto.add(new CustomLink(linkTo(methodOn(GiftCertificateController.class).deleteGiftCertificate(giftCertificate.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteOrder", "DELETE"));


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

        CollectionModel<GiftCertificateDTO> orderCollection = CollectionModel.of(giftCertificateDTOs);

        orderCollection.add(
                linkTo(
                        methodOn(GiftCertificateController.class)
                                .getGiftCertificates(search, tags, page, size, sortParams)
                ).withSelfRel());
        orderCollection.add(
                linkTo(
                        methodOn(GiftCertificateController.class)
                                .getGiftCertificates(search, tags, 0, size, sortParams)
                ).withRel("first"));
        if (page > 0) {
            orderCollection.add(
                    linkTo(methodOn(GiftCertificateController.class)
                            .getGiftCertificates(search, tags, page - 1, size, sortParams)
                    ).withRel("previous"));
        }
        if (!orders.isEmpty()) {
            orderCollection.add(
                    linkTo(methodOn(GiftCertificateController.class)
                            .getGiftCertificates(search, tags, page + 1, size, sortParams)
                    ).withRel("next"));
        }
        orderCollection.add(new CustomLink(linkTo(methodOn(GiftCertificateController.class).createGiftCertificate(null))
                .toUriComponentsBuilder().toUriString(), "create gift certificate", "POST"));


        return orderCollection;
    }
}
