package com.epam.esm.controller;

import com.epam.esm.exception.ErrorResponse;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.filter.GiftCertificateFilter;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.service.impl.GiftCertificateServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.util.Constants.*;

@RestController
@RequestMapping("/certificates")
public class GiftCertificateController {
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateServiceImpl.class);
    private final GiftCertificateServiceImpl giftCertificateService;

    @Autowired
    public GiftCertificateController(GiftCertificateServiceImpl giftCertificateService) {
        this.giftCertificateService = giftCertificateService;
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getGiftCertificateById(@PathVariable Long id) {
        try {
            Optional<GiftCertificate> certificate = giftCertificateService.getGiftCertificateById(id);
            if (certificate.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested certificate not found (id = " + id + ")", "40401"));
            return ResponseEntity.ok(certificate.get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @PostMapping
    public ResponseEntity<?> createGiftCertificate(@RequestBody GiftCertificate certificate) {
        try {
            GiftCertificate createdCertificate = giftCertificateService.createGiftCertificate(certificate);
            return ResponseEntity.ok(createdCertificate);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateGiftCertificate(@PathVariable Long id,
                                                   @RequestBody GiftCertificate certificate) {
        try {
            Optional<GiftCertificate> updatedCertificate = giftCertificateService.updateGiftCertificate(id, certificate);
            return updatedCertificate
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGiftCertificate(@PathVariable Long id) {
        try {
            boolean isDeleted = giftCertificateService.deleteGiftCertificate(id);
            return isDeleted ?
                    ResponseEntity.noContent().build() :
                    ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(new ErrorResponse("Requested certificate not found (id = " + id + ")", "40401"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    @GetMapping
    public ResponseEntity<?> getGiftCertificates(
            @RequestParam(name = "search", required = false) String searchQuery,
            @RequestParam(name = "tag", required = false) String tagName,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams
    ) {
        try {
            GiftCertificateFilter giftCertificateFilter = GiftCertificateFilter.builder()
                    .withTagName(tagName)
                    .withSearchQuery(searchQuery)
                    .build();
            List<GiftCertificate> certificates = giftCertificateService.getGiftCertificates(giftCertificateFilter, page, size, sortParams);
            return ResponseEntity.ok(certificates);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }
}
