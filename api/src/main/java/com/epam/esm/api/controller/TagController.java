package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.TagAssembler;
import com.epam.esm.api.dto.TagDTO;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.api.util.Constants.*;

@RestController
@RequestMapping("/tags")
public class TagController {
    private final TagService tagService;
    private final TagAssembler tagAssembler;

    @Autowired
    public TagController(TagService tagService, TagAssembler tagAssembler) {
        this.tagService = Objects.requireNonNull(tagService, "TagService must be initialised");
        this.tagAssembler = Objects.requireNonNull(tagAssembler, "TagAssembler must be initialised");
    }

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> getTags(
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams) {
        try {
            List<Tag> tags = tagService.getTags(page, size, sortParams);
            if (tags.size() > 0) {
                CollectionModel<TagDTO> tagCollection = tagAssembler.toCollectionModel(tags, page, size, sortParams);
                return ResponseEntity.ok(tagCollection);
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found", "40401"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @PostMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> addTag(@RequestBody Tag tag) {
        try {
            return ResponseEntity.ok(tagAssembler.toSingleModel(tagService.createTag(tag)));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getTagById(@PathVariable Long id) {
        try {
            Optional<Tag> tag = tagService.getTagById(id);
            if (tag.isPresent()) return ResponseEntity.ok(tagAssembler.toSingleModel(tag.get()));
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found (id = " + id + ")", "40401"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTagById(@PathVariable Long id) {
        try {
            tagService.deleteTag(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }
}
