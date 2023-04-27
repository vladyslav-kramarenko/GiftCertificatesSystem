package com.epam.esm.api.assembler.giftCertificate;
import com.epam.esm.api.assembler.tag.NestedTagAssembler;
import com.epam.esm.api.assembler.tag.TagAssembler;
import com.epam.esm.api.dto.giftCertificate.BaseGiftCertificateDTO;
import org.springframework.stereotype.Component;

@Component
public class NestedGiftCertificateAssembler extends GiftCertificateAssemblerBase<BaseGiftCertificateDTO> {
    protected NestedGiftCertificateAssembler(TagAssembler tagAssembler, NestedTagAssembler nestedTagAssembler) {
        super(nestedTagAssembler, tagAssembler);
    }
    @Override
    protected BaseGiftCertificateDTO createDtoInstance() {
        return new BaseGiftCertificateDTO();
    }
}