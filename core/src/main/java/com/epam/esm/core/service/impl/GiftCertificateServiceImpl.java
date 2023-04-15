package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.DbException;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.filter.GiftCertificateFilter;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.service.GiftCertificateService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.epam.esm.core.util.CoreConstants.ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS;
import static com.epam.esm.core.util.CoreConstants.ALLOWED_SORT_DIRECTIONS;
import static com.epam.esm.core.util.GiftCertificateUtils.*;
import static com.epam.esm.core.util.SortUtilities.createSort;
import static com.epam.esm.core.util.TagUtils.validateTags;
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
        this.giftCertificateRepository = giftCertificateRepository;
        this.tagRepository = tagRepository;
    }

    /**
     * Retrieves a gift certificate by its ID.
     *
     * @param id a gift certificate ID
     * @return an {@link Optional} containing the retrieved gift certificate or empty if the gift
     * certificate does not exist
     * @throws IllegalArgumentException if the provided ID is invalid
     * @throws ServiceException         if an error occurred while retrieving the gift certificate from the database
     */
    @Override
    public Optional<GiftCertificate> getGiftCertificateById(Long id)
            throws IllegalArgumentException, ServiceException {
        validateId(id);
        try {
            Optional<GiftCertificate> giftCertificateOpt = giftCertificateRepository.findById(id);
            if (giftCertificateOpt.isPresent()) {
                GiftCertificate giftCertificate = giftCertificateOpt.get();
                Hibernate.initialize(giftCertificate.getTags());
                return Optional.of(giftCertificate);
            } else return Optional.empty();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while searching a gift certificate with id =" + id + "; ");
        }
    }

    /**
     * Creates a new gift certificate in the database.
     *
     * @param giftCertificate a {@link com.epam.esm.core.entity.GiftCertificate} object to create
     * @return the created gift certificate
     * @throws IllegalArgumentException if any of the fields of the provided gift certificate are invalid
     * @throws ServiceException         if an error occurred while creating the gift certificate in the database
     */
    @Override
    @Transactional(rollbackFor = ServiceException.class)
    public GiftCertificate createGiftCertificate(GiftCertificate giftCertificate)
            throws IllegalArgumentException, ServiceException {
        validateForNull(giftCertificate.getDescription(), "description");
        validateForNull(giftCertificate.getName(), "name");
        validateForNull(giftCertificate.getPrice(), "price");
        validateForNull(giftCertificate.getDuration(), "duration");

        validateGiftCertificateParams(giftCertificate);
        validateTags(giftCertificate.getTags());

        try {
            giftCertificateRepository.save(giftCertificate);
            addTags(giftCertificate);

            Optional<GiftCertificate> CreatedGiftCertificate = giftCertificateRepository.findById(giftCertificate.getId());
            if (CreatedGiftCertificate.isEmpty()) throw new DbException("Cannot find created gift certificate by id");
            return CreatedGiftCertificate.get();
        } catch (DbException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while creating a gift certificate");
        }
    }


    private void validateGiftCertificateParams(GiftCertificate giftCertificate) {
        validateGiftCertificateDescription(giftCertificate.getDescription());
        validateGiftCertificateName(giftCertificate.getName());
        validateGiftCertificatePrice(giftCertificate.getPrice());
        validateGiftCertificateDuration(giftCertificate.getDuration());
    }

    private void addTags(GiftCertificate giftCertificate) throws DbException {
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
    public Optional<GiftCertificate> updateGiftCertificate(Long id, GiftCertificate giftCertificate) throws IllegalArgumentException, ServiceException {
        validateId(id);
        validateGiftCertificateParams(giftCertificate);

        try {
            Optional<GiftCertificate> optionalOldGiftCertificate = giftCertificateRepository.findById(id);
            if (optionalOldGiftCertificate.isEmpty()) return Optional.empty();
            GiftCertificate oldGiftCertificate = optionalOldGiftCertificate.get();

            updateCertificate(oldGiftCertificate, giftCertificate);
            giftCertificateRepository.save(oldGiftCertificate);

            if (giftCertificate.getTags() != null) {
                validateTags(giftCertificate.getTags());
                giftCertificateRepository.deleteAllTagsForCertificateById(oldGiftCertificate.getId());
                addTags(oldGiftCertificate);
            }
            return giftCertificateRepository.findById(id);
        } catch (DbException e) {
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
     * @throws ServiceException         if an error occurs while deleting the gift certificate
     */
    @Override
    public void deleteGiftCertificate(Long id) throws IllegalArgumentException, ServiceException {
        validateId(id);
        try {
            giftCertificateRepository.deleteById(id);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while deleting a gift certificate");
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
     * @throws ServiceException if an error occurs while getting the gift certificates
     */
    @Override
    @Transactional
    public List<GiftCertificate> getGiftCertificates(GiftCertificateFilter giftCertificateFilter, int page, int size, String[] sortParams) throws ServiceException {
        try {
            Optional<Sort> sort = createSort(sortParams, ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
            Pageable pageable = PageRequest.of(page, size, sort.orElse(Sort.by("id").ascending()));
            Stream<GiftCertificate> giftCertificates;

            String searchQuery = giftCertificateFilter.getSearchQuery();

            if (searchQuery == null || searchQuery.isEmpty()) {
                giftCertificates = giftCertificateRepository.findAll(pageable).stream();
            } else {
                String sortConditions = concatSort(sort.get(), "id asc");
                giftCertificates = giftCertificateRepository.findAll(
                        giftCertificateFilter.getSearchQuery(),
                        sortConditions,
                        page * size,
                        page
                ).stream();
            }
            List<GiftCertificate> giftCertificatesList = giftCertificateFilter.filter(giftCertificates)
                    .toList();

            for (GiftCertificate giftCertificate : giftCertificatesList) {
                Hibernate.initialize(giftCertificate.getTags());
            }

            return giftCertificatesList;
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());
        }
    }
}
