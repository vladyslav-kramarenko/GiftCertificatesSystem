package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.service.GiftCertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

import static com.epam.esm.core.repository.specs.GiftCertificateSpecification.*;
import static com.epam.esm.core.util.CoreConstants.ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS;
import static com.epam.esm.core.util.CoreConstants.ALLOWED_SORT_DIRECTIONS;
import static com.epam.esm.core.util.GiftCertificateUtils.*;
import static com.epam.esm.core.util.SortUtilities.createSort;
import static com.epam.esm.core.util.Utilities.*;

/**
 * Implementation of {@link GiftCertificateService} interface. Provides methods for
 * <p>
 * creating, retrieving, updating and deleting gift certificates from the database.
 */
@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateServiceImpl.class);
    private final GiftCertificateRepository giftCertificateRepository;
    private final TagRepository tagRepository;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateRepository giftCertificateRepository, TagRepository tagRepository) {
        this.giftCertificateRepository = Objects.requireNonNull(giftCertificateRepository, "GiftCertificateRepository must be initialised");
        this.tagRepository = Objects.requireNonNull(tagRepository, "TagRepository must be initialised");
    }

    /**
     * Retrieves a gift certificate by its ID.
     *
     * @param id a gift certificate ID
     * @return an {@link Optional} containing the retrieved gift certificate or empty if the gift
     * certificate does not exist
     */
    @Override
    public Optional<GiftCertificate> getGiftCertificateById(Long id) {
        return giftCertificateRepository.findByIdWithTags(id);
    }

    /**
     * Gets a list of gift certificates that match the specified filter, sorted and paginated as specified.
     *
     * @param page       the page number to return (starting at 0)
     * @param size       the number of gift certificates to return per page
     * @param sortParams an array of sort parameters in the format {field},{direction}, where field is one of the allowed
     *                   sort fields and direction is one of the allowed sort directions. If no sort parameters are
     *                   specified, the gift certificates are returned in their natural order.
     * @return a list of gift certificates that match the specified filter, sorted and paginated as specified
     */
    @Override
    @Transactional
    public List<GiftCertificate> getGiftCertificates(String searchQuery, String[] tags, int page, int size, String[] sortParams) {
        logger.info("page size = " + size);
        logger.info("page # = " + page);
        logger.info("pageLimit = " + page * size);
        Optional<Sort> sort = createSort(sortParams, ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        String tagsFilter = tags != null ? String.join(",", tags) : "";
        String sortConditions = concatSort(sort.orElse(null), "id asc");
        if (searchQuery == null) searchQuery = "";
        logger.info("searchQuery = " + searchQuery);
        logger.info("sortConditions = " + sortConditions);
        logger.info("tagsFilter = " + tagsFilter);
        List<GiftCertificate> result = new ArrayList<>(giftCertificateRepository.findAll(
                searchQuery,
                sortConditions,
                page * size,
                size,
                tagsFilter
        ));
        logger.info("List<GiftCertificate>.size = " + result.size());
        return result;
    }

    @Override
    public List<GiftCertificate> searchGiftCertificates(String searchTerm, int page, int size, Integer minPrice, Integer maxPrice, String[] sortParams) {


        Optional<Sort> sort = createSort(sortParams, ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        logger.info("searchTerm = " + searchTerm);

        Specification<GiftCertificate> specification = Specification
                .where(hasNameLike(searchTerm))
                .or(hasDescriptionLike(searchTerm))
                .or(hasTagLike(searchTerm));

        if (minPrice != null && minPrice > 0) {
            logger.info("minPrice = " + minPrice);
            specification = specification.and(hasPriceGreaterThanOrEqual(BigDecimal.valueOf(minPrice)));
        }

        if (maxPrice != null && maxPrice > 0) {
            logger.info("maxPrice = " + maxPrice);
            specification = specification.and(hasPriceLessThanOrEqual(BigDecimal.valueOf(maxPrice)));
        }

        Pageable sortedByPriceDesc = PageRequest.of(page, size, sort.orElse(Sort.by(Sort.Direction.ASC, "id")));

        List<GiftCertificate> giftCertificates = giftCertificateRepository.findAll(specification, sortedByPriceDesc);
        for(GiftCertificate cert:giftCertificates){
            logger.info(cert.getName());
            logger.info(cert.getDescription());
            logger.info(cert.getTags().toString());
        }
        return giftCertificates;
    }

    /**
     * Creates a new gift certificate in the database.
     *
     * @param giftCertificate a {@link com.epam.esm.core.entity.GiftCertificate} object to create
     * @return the created gift certificate
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public GiftCertificate createGiftCertificate(GiftCertificate giftCertificate) throws ServiceException {
        giftCertificate.setTags(updateTagsFromBd(giftCertificate.getTags()));
        giftCertificateRepository.save(giftCertificate);
        return giftCertificate;
    }

    private void addTagsToGiftCertificate(GiftCertificate giftCertificate, List<Tag> tags) {
        for (Tag tag : tags) {
            giftCertificateRepository.addTagToCertificate(giftCertificate, tag);
        }
    }

    private List<Tag> updateTagsFromBd(List<Tag> tags) throws ServiceException {
        try {
            if (tags != null) {
                return tags.stream().map(tag -> {
                    Optional<Tag> existingTagOpt = tagRepository.getByName(tag.getName());
                    return existingTagOpt.orElseGet(() -> tagRepository.save(tag));
                }).toList();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while updating tags");
        }
        return new ArrayList<>();
    }

    /**
     * Updates an existing gift certificate in the database.
     *
     * @param id              the ID of the gift certificate to update
     * @param giftCertificate a {@link com.epam.esm.core.entity.GiftCertificate} object containing the new data
     * @return an {@link Optional} containing the updated gift certificate or empty if the gift
     * certificate does not exist
     * @throws ServiceException if an error occurred while updating the gift certificate in the database
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public Optional<GiftCertificate> updateGiftCertificate(Long id, GiftCertificate giftCertificate) throws ServiceException {
        try {
            Optional<GiftCertificate> optionalOldGiftCertificate = giftCertificateRepository.findById(id);
            if (optionalOldGiftCertificate.isEmpty()) return Optional.empty();
            GiftCertificate oldGiftCertificate = optionalOldGiftCertificate.get();

            updateCertificate(oldGiftCertificate, giftCertificate);
            giftCertificateRepository.save(oldGiftCertificate);

            if (giftCertificate.getTags() != null) {
                giftCertificateRepository.deleteAllTagsForCertificateById(oldGiftCertificate.getId());
                List<Tag> updatedTags = updateTagsFromBd(giftCertificate.getTags());
                addTagsToGiftCertificate(oldGiftCertificate, updatedTags);
            }
            return giftCertificateRepository.findById(id);
        } catch (ServiceException e) {
            throw new ServiceException(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while updating a gift certificate");
        }
    }

    /**
     * Deletes a gift certificate with the specified id.
     *
     * @param id the id of the gift certificate to delete
     * @throws IllegalArgumentException if the specified Gift Certificate is used in an order
     */
    @Override
    public void deleteGiftCertificate(Long id) {
        try {
            giftCertificateRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new IllegalArgumentException("Cannot delete Gift Certificate that used in an order");
        }
    }
}
