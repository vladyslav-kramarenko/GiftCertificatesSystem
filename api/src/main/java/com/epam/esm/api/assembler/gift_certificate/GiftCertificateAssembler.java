package com.epam.esm.api.assembler.gift_certificate;

import com.epam.esm.api.assembler.tag.NestedTagAssembler;
import com.epam.esm.api.assembler.tag.TagAssembler;
import com.epam.esm.api.controller.GiftCertificateController;
import com.epam.esm.api.dto.giftCertificate.SingleGiftCertificateDTO;
import com.epam.esm.api.dto.TagDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

import static com.epam.esm.api.util.Constants.METHOD_DELETE;
import static com.epam.esm.api.util.Constants.METHOD_PUT;
import static com.epam.esm.api.util.LinksUtils.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public
class GiftCertificateAssembler extends GiftCertificateAssemblerBase<SingleGiftCertificateDTO> {
    protected GiftCertificateAssembler(NestedTagAssembler nestedTagAssembler, TagAssembler tagAssembler) {
        super(nestedTagAssembler, tagAssembler);
    }

    @Override
    protected SingleGiftCertificateDTO createDtoInstance() {
        return new SingleGiftCertificateDTO();
    }

    private SingleGiftCertificateDTO mapGiftCertificateAndAddLinks(GiftCertificate giftCertificate, Function<Tag, TagDTO> tagMapper) throws ServiceException {
        SingleGiftCertificateDTO dto = createDtoInstance();
        GiftCertificateMapper.mapGiftCertificateToDto(giftCertificate, dto);

        dto.add(getGiftCertificateSelfLink(giftCertificate.getId()));
        dto.setTags(giftCertificate.getTags().stream().map(tagMapper).toList());
        return dto;
    }

    @SneakyThrows(ServiceException.class)
    @Override
    public SingleGiftCertificateDTO toModel(GiftCertificate giftCertificate) {
        return mapGiftCertificateAndAddLinks(giftCertificate, nestedTagAssembler::toModel);
    }

    public SingleGiftCertificateDTO toSingleModel(GiftCertificate giftCertificate) throws ServiceException {
        SingleGiftCertificateDTO dto = mapGiftCertificateAndAddLinks(giftCertificate, tagAssembler::toModel);
        dto.add(new CustomLink(linkTo(methodOn(GiftCertificateController.class).updateGiftCertificate(giftCertificate.getId(), giftCertificate))
                .toUriComponentsBuilder().toUriString(), "updateGiftCertificate", METHOD_PUT));
        dto.add(new CustomLink(linkTo(methodOn(GiftCertificateController.class).deleteGiftCertificate(giftCertificate.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteGiftCertificate", METHOD_DELETE));
        dto.add(getCreateGiftCertificateLink());
        return dto;
    }

    public CollectionModel<SingleGiftCertificateDTO> toCollectionModel(
            List<GiftCertificate> orders,
            String search,
            String[] tags,
            int page,
            int size,
            String[] sortParams
    ) throws ServiceException {
        List<SingleGiftCertificateDTO> giftCertificateDTOs = orders.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<SingleGiftCertificateDTO> collection = CollectionModel.of(giftCertificateDTOs);
        addOrderNavigationLinks(collection, orders, search, tags, page, size, sortParams);
        return collection;
    }
}