package com.epam.esm.api.assembler.gift_certificate;

import com.epam.esm.api.assembler.tag.NestedTagAssembler;
import com.epam.esm.api.assembler.tag.TagAssembler;
import com.epam.esm.api.dto.giftCertificate.BaseGiftCertificateDTO;
import com.epam.esm.core.entity.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.RepresentationModelAssembler;

import java.util.Objects;

public abstract class GiftCertificateAssemblerBase<T extends BaseGiftCertificateDTO> implements RepresentationModelAssembler<GiftCertificate, T> {
    @Autowired
    protected GiftCertificateAssemblerBase(NestedTagAssembler nestedTagAssembler, TagAssembler tagAssembler) {
        this.tagAssembler = Objects.requireNonNull(tagAssembler, "TagAssembler must be initialised");
        this.nestedTagAssembler = Objects.requireNonNull(nestedTagAssembler, "NestedTagAssembler must be initialised");
    }

    protected abstract T createDtoInstance();

    protected final NestedTagAssembler nestedTagAssembler;
    protected final TagAssembler tagAssembler;

    @Override
    public T toModel(GiftCertificate giftCertificate) {
        T dto = createDtoInstance();
        return GiftCertificateMapper.mapGiftCertificateToDto(giftCertificate, dto);
    }
}

