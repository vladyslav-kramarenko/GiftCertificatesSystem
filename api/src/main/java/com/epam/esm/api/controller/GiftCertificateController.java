package com.epam.esm.api.controller;

import com.epam.esm.api.ErrorResponse;
import com.epam.esm.api.assembler.giftCertificate.GiftCertificateAssembler;
import com.epam.esm.core.entity.OnCreate;
import com.epam.esm.core.entity.OnUpdate;
import com.epam.esm.core.service.GiftCertificateService;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.entity.GiftCertificate;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.api.util.Constants.*;

/**
 * Controller class for handling gift certificate related HTTP requests.
 */
@RestController
@RequestMapping("/certificates")
@Validated
public class GiftCertificateController {
    /**
     * Service class for gift certificate operations.
     */
    private final GiftCertificateService giftCertificateService;
    private final GiftCertificateAssembler giftCertificateAssembler;
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateController.class);

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
        this.giftCertificateService = Objects.requireNonNull(giftCertificateService, "GiftCertificateService must be initialised");
        this.giftCertificateAssembler = Objects.requireNonNull(giftCertificateAssembler, "GiftCertificateAssembler must be initialised");
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
    public ResponseEntity<?> getGiftCertificateById(@PathVariable @Min(0) Long id) throws ServiceException {
        Optional<GiftCertificate> certificate = giftCertificateService.getGiftCertificateById(id);
        if (certificate.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Requested certificate not found (id = " + id + ")", ERROR_CODE_40401));
        return ResponseEntity.ok(giftCertificateAssembler.toSingleModel(certificate.get()));
    }

    /**
     * Creates a new gift certificate.
     *
     * @param certificate a {@link GiftCertificate} object to create
     * @return a {@link ResponseEntity} containing the created gift certificate or an error message if any of the fields
     * of the provided gift certificate are invalid or an error occurs while creating it in the database
     */
    @PostMapping
    public ResponseEntity<?> createGiftCertificate(@RequestBody @NotNull @Validated(OnCreate.class) GiftCertificate certificate) throws ServiceException {
        GiftCertificate createdCertificate = giftCertificateService.createGiftCertificate(certificate);
        return ResponseEntity.ok(giftCertificateAssembler.toSingleModel(createdCertificate));
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
    public ResponseEntity<?> updateGiftCertificate(
            @PathVariable @Min(0) Long id,
            @RequestBody
            @Validated(OnUpdate.class)
            @NotNull
            GiftCertificate certificate) throws ServiceException {
        Optional<GiftCertificate> updatedCertificate = giftCertificateService.updateGiftCertificate(id, certificate);
        if (updatedCertificate.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(
                            "Requested certificate not found (id = " + id + ")", ERROR_CODE_40401)
                    );
        }
        return ResponseEntity.ok(giftCertificateAssembler.toSingleModel(updatedCertificate.get()));
    }

    /**
     * Deletes a gift certificate by its ID.
     *
     * @param id a gift certificate ID
     * @return a {@link ResponseEntity} with no content or an error message if the gift certificate does not exist or an
     * error occurs while deleting it from the database
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGiftCertificate(@PathVariable @Min(0) Long id) throws ServiceException {
        giftCertificateService.deleteGiftCertificate(id);
        return ResponseEntity.noContent().build();
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
            @RequestParam(name = "search", required = false)
            String searchQuery,
            @RequestParam(name = "tags", required = false)
            String[] tags,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE)
            @Min(value = 0, message = "Page number can't be negative")
            int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Min(value = 0, message = "Page size can't be negative")
            int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT)
            String[] sortParams
    ) throws ServiceException {
        List<GiftCertificate> certificates = giftCertificateService.getGiftCertificates(
                searchQuery, tags, page, size, sortParams);
        if (certificates.size() > 0) return ResponseEntity.ok(giftCertificateAssembler.toCollectionModel(
                certificates, searchQuery, tags, page, size, sortParams));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchGiftCertificates(
            @RequestParam(name = "searchTerm", required = false)
            String searchTerm,
            @RequestParam(name = "page", required = false, defaultValue = DEFAULT_PAGE)
            @Min(value = 0, message = "Page number can't be negative")
            int page,
            @RequestParam(name = "size", required = false, defaultValue = DEFAULT_PAGE_SIZE)
            @Min(value = 0, message = "Page size can't be negative")
            int size,
            @RequestParam(name = "sort", required = false, defaultValue = DEFAULT_SORT)
            String[] sortParams,
            @RequestParam(name = "minPrice", required = false)
            @Min(value = 0, message = "Page size can't be negative")
            int minPrice,
            @RequestParam(name = "maxPrice", required = false)
            @Min(value = 0, message = "Page size can't be negative")
            int maxPrice
    ) throws ServiceException {
        logger.info("sort_input = " + sortParams);
        List<GiftCertificate> certificates = giftCertificateService.searchGiftCertificates(
                searchTerm, page, size, minPrice, maxPrice, sortParams);
        if (certificates.size() > 0) {
            return ResponseEntity.ok(giftCertificateAssembler.toCollectionModel(
                    certificates, searchTerm, page, size, sortParams, minPrice, maxPrice));
        }
        return ResponseEntity.noContent().build();
    }
}
