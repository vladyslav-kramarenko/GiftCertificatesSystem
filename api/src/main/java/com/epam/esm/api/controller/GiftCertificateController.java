package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.GiftCertificateAssembler;
import com.epam.esm.core.service.GiftCertificateService;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.filter.GiftCertificateFilter;
import com.epam.esm.core.entity.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.api.util.Constants.*;

/**
 * Controller class for handling gift certificate related HTTP requests.
 */
@RestController
@RequestMapping("/certificates")
public class GiftCertificateController {
    /**
     * Service class for gift certificate operations.
     */
    private final GiftCertificateService giftCertificateService;
    private final GiftCertificateAssembler giftCertificateAssembler;

    /**
     * Constructs a new instance of {@code GiftCertificateController} with the given gift certificate service.
     *
     * @param giftCertificateService a {@link GiftCertificateService} object
     */
    @Autowired
    public GiftCertificateController(
            GiftCertificateService giftCertificateService,
            GiftCertificateAssembler giftCertificateAssembler
    ) {
        this.giftCertificateService = giftCertificateService;
        this.giftCertificateAssembler = giftCertificateAssembler;
    }


    /**
     * Retrieves a gift certificate by its ID.
     *
     * @param id a gift certificate ID
     * @return a {@link ResponseEntity} containing the retrieved gift certificate or an error message if the gift
     * certificate does not exist or an error occurs while retrieving it from the database
     */
    @GetMapping(value = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getGiftCertificateById(@PathVariable Long id) {
        try {
            Optional<GiftCertificate> certificate = giftCertificateService.getGiftCertificateById(id);
            if (certificate.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Requested certificate not found (id = " + id + ")", "40401"));
            return ResponseEntity.ok(giftCertificateAssembler.toModel(certificate.get()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    /**
     * Creates a new gift certificate.
     *
     * @param certificate a {@link GiftCertificate} object to create
     * @return a {@link ResponseEntity} containing the created gift certificate or an error message if any of the fields
     * of the provided gift certificate are invalid or an error occurs while creating it in the database
     */
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

    /**
     * Updates an existing gift certificate by its ID.
     *
     * @param id          a gift certificate ID
     * @param certificate a {@link GiftCertificate} object to update
     * @return a {@link ResponseEntity} containing the updated gift certificate or an error message if the gift
     * certificate does not exist or an error occurs while updating it in the database
     */
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

    /**
     * Deletes a gift certificate by its ID.
     *
     * @param id a gift certificate ID
     * @return a {@link ResponseEntity} with no content or an error message if the gift certificate does not exist or an
     * error occurs while deleting it from the database
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGiftCertificate(@PathVariable Long id) {
        try {
            giftCertificateService.deleteGiftCertificate(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }

    /**
     * Retrieves a list of gift certificates that match the specified filter, sorted and paginated as specified.
     *
     * @param searchQuery a search query to apply to the gift certificates
     * @param tags        tag names to filter gift certificates
     * @param page        the page number to return (starting at 0)
     * @param size        the number of gift certificates to return per page
     * @param sortParams  an array of sort parameters in the format {field},{direction},
     *                    where field is one of the allowed sort fields and direction is one of the allowed sort directions.
     *                    If no sort parameters are specified, the gift certificates are returned with the default sort.
     * @return a {@link ResponseEntity} containing a list of gift certificates that match the specified filter, sorted and
     * paginated as specified or an error message if an error occurs while getting the gift certificates
     */
    @GetMapping("")
    public ResponseEntity<?> getGiftCertificates(
            @RequestParam(name = "search", required = false) String searchQuery,
            @RequestParam(name = "tags", required = false) String[] tags,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE) int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE) int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT) String[] sortParams
    ) {
        try {
            GiftCertificateFilter giftCertificateFilter = GiftCertificateFilter.builder()
                    .withTags(tags)
                    .withSearchQuery(searchQuery)
                    .build();
            List<GiftCertificate> certificates = giftCertificateService.getGiftCertificates(giftCertificateFilter, page, size, sortParams);
            return ResponseEntity.ok(giftCertificateAssembler.toCollectionModel(certificates));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), "40001"));
        } catch (ServiceException e) {
            return ResponseEntity.internalServerError().body(new ErrorResponse(e.getMessage(), "50001"));
        }
    }
}
