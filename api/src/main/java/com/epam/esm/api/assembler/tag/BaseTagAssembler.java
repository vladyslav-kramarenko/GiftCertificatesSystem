package com.epam.esm.api.assembler.tag;

import com.epam.esm.api.dto.TagDTO;
import com.epam.esm.core.entity.Tag;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

@Component
public abstract class BaseTagAssembler implements RepresentationModelAssembler<Tag, TagDTO> {
    @Override
    public TagDTO toModel(Tag tag) {
        TagDTO tagDTO = new TagDTO();
        return mapTagToDto(tag, tagDTO);
    }

    protected TagDTO mapTagToDto(Tag tag, TagDTO dto) {
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }
}
