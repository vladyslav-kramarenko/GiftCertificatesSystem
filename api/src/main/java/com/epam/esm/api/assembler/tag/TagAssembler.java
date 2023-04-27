package com.epam.esm.api.assembler.tag;

import com.epam.esm.api.controller.TagController;
import com.epam.esm.api.dto.TagDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import lombok.SneakyThrows;
import org.springframework.hateoas.CollectionModel;
import org.springframework.stereotype.Component;


import java.util.List;

import static com.epam.esm.api.util.LinksUtils.getCreateTagLink;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagAssembler extends BaseTagAssembler {

    @SneakyThrows(ServiceException.class)
    @Override
    public TagDTO toModel(Tag tag) {
        return getTagDTO(tag);
    }

    public TagDTO toSingleModel(Tag tag) throws ServiceException {
        TagDTO tagDTO = getTagDTO(tag);
        tagDTO.add(new CustomLink(linkTo(methodOn(TagController.class).deleteTagById(tag.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteTag", "DELETE"));
        tagDTO.add(getCreateTagLink());
        return tagDTO;
    }

    private TagDTO getTagDTO(Tag tag) throws ServiceException {
        TagDTO tagDTO = new TagDTO();
        mapTagToDto(tag, tagDTO);
        tagDTO.add(new CustomLink(linkTo(methodOn(TagController.class).getTagById(tag.getId()))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        return tagDTO;
    }

    public CollectionModel<TagDTO> toCollectionModel(List<Tag> tags, int page, int size, String[] sortParams) throws ServiceException {
        List<TagDTO> tagDTOs = tags.stream()
                .map(this::toModel)
                .toList();

        CollectionModel<TagDTO> tagCollection = CollectionModel.of(tagDTOs);

        tagCollection.add(linkTo(methodOn(TagController.class).getTags(page, size, sortParams)).withSelfRel());
        tagCollection.add(linkTo(methodOn(TagController.class).getTags(0, size, sortParams)).withRel("first"));
        if (page > 0) {
            tagCollection.add(linkTo(methodOn(TagController.class).getTags(page - 1, size, sortParams)).withRel("previous"));
        }
        if (!tags.isEmpty()) {
            tagCollection.add(linkTo(methodOn(TagController.class).getTags(page + 1, size, sortParams)).withRel("next"));
        }
        tagCollection.add(getCreateTagLink());
        return tagCollection;
    }
}
