package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.tag.TagAssembler;
import com.epam.esm.api.dto.TagDTO;
import com.epam.esm.core.dto.MostUsedTagDTO;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.service.TagService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.epam.esm.api.util.Constants.*;

@RestController
@RequestMapping("/tags")
@Validated
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
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE)
            @Min(value = 0, message = "Page number can't be negative")
            int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Min(value = 0, message = "Page size can't be negative")
            int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT)
            String[] sortParams
    ) throws ServiceException {
        List<Tag> tags = tagService.getTags(page, size, sortParams);
        if (tags.size() > 0) {
            CollectionModel<TagDTO> tagCollection = tagAssembler.toCollectionModel(tags, page, size, sortParams);
            return ResponseEntity.ok(tagCollection);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Requested resource not found", ERROR_CODE_40401));
    }

    @PostMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> addTag(@RequestBody @Valid @NotNull Tag tag) throws ServiceException {
        return ResponseEntity.ok(tagAssembler.toSingleModel(tagService.createTag(tag)));
    }
////////////////////////////////////////////////////////////////
    @PostMapping(value = "/custom")
    @ResponseBody
    public ResponseEntity<?> addTagCustom(@Valid @RequestBody Tag tag, BindingResult bindingResult) throws ServiceException {
        if (bindingResult.hasErrors()) {
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : fieldErrors) {
                errors.put(error.getField()+" (custom)", error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }
        Tag createdTag = tagService.createTag(tag);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }
////////////////////////////////////////////////////////////////////////
    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getTagById(@PathVariable @Min(1) Long id) throws ServiceException {
        Optional<Tag> tag = tagService.getTagById(id);
        if (tag.isPresent()) return ResponseEntity.ok(tagAssembler.toSingleModel(tag.get()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Requested resource not found (id = " + id + ")", ERROR_CODE_40401));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteTagById(@PathVariable @Min(1) Long id) throws ServiceException {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/most-used")
    public ResponseEntity<?> getMostWidelyUsedTagWithHighestCostByUserId(
            @RequestParam @NotNull
            @Min(value = 0, message = "User ID can't be negative")
            Long userId,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE)
            @Min(value = 0, message = "Page number can't be negative")
            int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Min(value = 0, message = "Page size can't be negative")
            int size) throws ServiceException {
        Optional<Tag> tag = tagService.getMostWidelyUsedTagWithHighestCostByUserId(userId, page, size);
        if (tag.isPresent()) return ResponseEntity.ok(tagAssembler.toSingleModel(tag.get()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Requested resource not found (user ID = " + userId + ")", ERROR_CODE_40401));

    }

    @GetMapping("/most-used-ext")
    public ResponseEntity<?> getMostWidelyUsedTagWithHighestCostByUserIdExtended(
            @RequestParam @NotNull
            @Min(value = 0, message = "User ID can't be negative")
            Long userId,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE)
            @Min(value = 0, message = "Page number can't be negative")
            int page,
            @RequestParam(name = "size", required = false, defaultValue = "1")
            @Min(value = 0, message = "Page size can't be negative")
            int size) {
        List<MostUsedTagDTO> tags = tagService.getMostWidelyUsedTagWithHighestCostByUserIdExtended(userId, page, size);
        if (tags.size() > 0) {
            return ResponseEntity.ok(tags);
        }
        return ResponseEntity.notFound().build();
    }
}
