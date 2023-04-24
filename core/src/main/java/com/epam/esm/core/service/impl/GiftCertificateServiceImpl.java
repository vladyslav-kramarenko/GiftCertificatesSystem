package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.filter.GiftCertificateFilter;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.service.GiftCertificateService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
     * @throws IllegalArgumentException if the provided ID is invalid
 */
    @Override
    public Optional<GiftCertificate> getGiftCertificateById(Long id) {
            Optional<GiftCertificate> giftCertificateOpt = giftCertificateRepository.findById(id);
            if (giftCertificateOpt.isPresent()) {
                GiftCertificate giftCertificate = giftCertificateOpt.get();
                //TODO remove Hibernate.initialize
                Hibernate.initialize(giftCertificate.getTags());
                return Optional.of(giftCertificate);
            } else return Optional.empty();
    }

    /**
     * Creates a new gift certificate in the database.
     *
     * @param giftCertificate a {@link com.epam.esm.core.entity.GiftCertificate} object to create
     * @return the created gift certificate
     * @throws ServiceException         if an error occurred while creating the gift certificate in the database
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public GiftCertificate createGiftCertificate(GiftCertificate giftCertificate)
            throws IllegalArgumentException, ServiceException {
            giftCertificateRepository.save(giftCertificate);
            addTags(giftCertificate);

            Optional<GiftCertificate> CreatedGiftCertificate = giftCertificateRepository.findById(giftCertificate.getId());
            if (CreatedGiftCertificate.isEmpty()) throw new ServiceException("Cannot find created gift certificate by id");
            return CreatedGiftCertificate.get();
    }

    private void addTags(GiftCertificate giftCertificate) {
        List<Tag> tags = giftCertificate.getTags();
        if (tags != null) {
            for (Tag tag : tags) {
                Tag newTag;
                Optional<Tag> existingTag = tagRepository.getByName(tag.getName());
                newTag = existingTag.map(value -> new Tag(value.getId(), value.getName())).orElseGet(() -> tagRepository.save(tag));
                giftCertificateRepository.addTagToCertificate(giftCertificate, newTag);
            }
        }
    }

    /**
     * Updates an existing gift certificate in the database.
     *
     * @param id              the ID of the gift certificate to update
     * @param giftCertificate a {@link com.epam.esm.core.entity.GiftCertificate} object containing the new data
     * @return an {@link Optional} containing the updated gift certificate or empty if the gift
     * certificate does not exist
     * @throws IllegalArgumentException if the provided ID is invalid or any of the fields of the provided gift
     *                                  certificate are invalid
     * @throws ServiceException         if an error occurred while updating the gift certificate in the database
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
                addTags(oldGiftCertificate);
            }
            return giftCertificateRepository.findById(id);
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
     * @throws IllegalArgumentException if the specified id is null or negative
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

    /**
     * Gets a list of gift certificates that match the specified filter, sorted and paginated as specified.
     *
     * @param giftCertificateFilter the filter to apply to the gift certificates
     * @param page                  the page number to return (starting at 0)
     * @param size                  the number of gift certificates to return per page
     * @param sortParams            an array of sort parameters in the format {field},{direction}, where field is one of the allowed
     *                              sort fields and direction is one of the allowed sort directions. If no sort parameters are
     *                              specified, the gift certificates are returned in their natural order.
     * @return a list of gift certificates that match the specified filter, sorted and paginated as specified
     */
    @Override
    @Transactional
    public List<GiftCertificate> getGiftCertificates(
            GiftCertificateFilter giftCertificateFilter,
            int page,
            int size,
            String[] sortParams
    ) {
            Optional<Sort> sort = createSort(sortParams, ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
            String tagsFilter = String.join(",", giftCertificateFilter.getTags());
            String sortConditions = concatSort(sort.orElse(null), "id asc");
            List<GiftCertificate> giftCertificatesList = giftCertificateRepository.findAll(
                    giftCertificateFilter.getSearchQuery(),
                    sortConditions,
                    page * size,
                    size,
                    tagsFilter
            );
            for (GiftCertificate giftCertificate : giftCertificatesList) {
                Hibernate.initialize(giftCertificate.getTags());
            }
            return giftCertificatesList;
    }
}
