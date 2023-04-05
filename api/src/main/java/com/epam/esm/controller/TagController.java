package com.epam.esm.controller;

import com.epam.esm.ErrorResponse;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.Tag;
import com.epam.esm.service.impl.TagServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.util.Constants.DEFAULT_SORT;

@RestController
@RequestMapping("/tags")
public class TagController {
    private final TagServiceImpl tagService;

    @Autowired
    public TagController(TagServiceImpl tagService) {
        this.tagService = tagService;
    }

    @GetMapping(value = "")
    @ResponseBody
    public ResponseEntity<?> getTags(
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams) {
        try {
            List<Tag> tags = tagService.getTags(sortParams);
            if (tags.size() > 0) return ResponseEntity.ok(tags);
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
            return ResponseEntity.ok(tagService.createTag(tag));
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
            if (tag.isPresent()) return ResponseEntity.ok(tag.get());
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
            boolean isDeleted = tagService.deleteTag(id);
            if (isDeleted) return ResponseEntity.noContent().build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested resource not found (id = " + id + ")", "40401"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }
}
