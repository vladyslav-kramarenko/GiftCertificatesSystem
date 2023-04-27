package com.epam.esm.api.dto.giftCertificate;

import com.epam.esm.api.dto.TagDTO;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class SingleGiftCertificateDTO extends BaseGiftCertificateDTO {
    private List<TagDTO> tags;
}
