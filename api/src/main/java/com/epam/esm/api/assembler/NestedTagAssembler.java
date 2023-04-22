package com.epam.esm.api.assembler;

import com.epam.esm.api.dto.TagDTO;
import com.epam.esm.core.entity.Tag;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public class NestedTagAssembler implements RepresentationModelAssembler<Tag, TagDTO> {

    @Override
    public TagDTO toModel(Tag tag) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());
        return tagDTO;
    }
}
