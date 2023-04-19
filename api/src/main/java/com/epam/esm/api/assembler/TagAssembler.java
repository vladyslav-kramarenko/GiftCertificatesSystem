package com.epam.esm.api.assembler;

import com.epam.esm.api.controller.TagController;
import com.epam.esm.api.dto.TagDTO;
import com.epam.esm.api.util.CustomLink;
import com.epam.esm.core.entity.Tag;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TagAssembler implements RepresentationModelAssembler<Tag, TagDTO> {

    @Override
    public TagDTO toModel(Tag tag) {
        TagDTO tagDTO = new TagDTO();
        tagDTO.setId(tag.getId());
        tagDTO.setName(tag.getName());

        tagDTO.add(new CustomLink(linkTo(methodOn(TagController.class).getTagById(tag.getId()))
                .toUriComponentsBuilder().toUriString(), "self", "GET"));
        tagDTO.add(new CustomLink(linkTo(methodOn(TagController.class).deleteTagById(tag.getId()))
                .toUriComponentsBuilder().toUriString(), "deleteTag", "DELETE"));
        tagDTO.add(new CustomLink(linkTo(methodOn(TagController.class).addTag(null)).toUriComponentsBuilder()
                .toUriString(), "createTag", "POST"));


//        tagDTO.add(linkTo(methodOn(TagController.class)
//                .getTagById(tag.getId()))
//                .withSelfRel());
//        tagDTO.add(linkTo(methodOn(TagController.class)
//                .deleteTagById(tag.getId()))
//                .withRel("deleteTag"));
//        tagDTO.add(linkTo(methodOn(TagController.class)
//                .addTag(null))
//                .withRel("createTag"));
        return tagDTO;
    }

    public CollectionModel<TagDTO> toCollectionModel(List<Tag> tags, int page, int size, String[] sortParams) {
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
        // Assuming you have a method to calculate the last page number
//        int lastPage = getLastPageNumber();
//        tagCollection.add(linkTo(methodOn(TagController.class).getTags(lastPage, size, sortParams)).withRel("last"));

        return tagCollection;
    }
}
