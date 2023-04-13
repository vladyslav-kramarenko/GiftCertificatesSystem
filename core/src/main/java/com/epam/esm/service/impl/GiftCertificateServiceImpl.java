package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.filter.GiftCertificateFilter;
import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.service.GiftCertificateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.util.CoreConstants.*;
import static com.epam.esm.util.GiftCertificateUtils.*;
import static com.epam.esm.util.SortUtilities.createSort;
import static com.epam.esm.util.TagUtils.validateTags;
import static com.epam.esm.util.Utilities.validateId;

/**
 * Implementation of {@link GiftCertificateService} interface. Provides methods for
 * <p>
 * creating, retrieving, updating and deleting gift certificates from the database.
 */
@Service
public class GiftCertificateServiceImpl implements GiftCertificateService {
    private static final Logger logger = LoggerFactory.getLogger(GiftCertificateServiceImpl.class);
    private final GiftCertificateDao giftCertificateDao;
    private final TagDao tagDao;

    /**
     * Constructs a new instance of {@code GiftCertificateServiceImpl} with the given gift certificate DAO
     *
     * @param giftCertificateDao a {@link com.epam.esm.dao.GiftCertificateDao} object
     */
    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao giftCertificateDao, TagDao tagDao) {
        this.giftCertificateDao = giftCertificateDao;
        this.tagDao = tagDao;
    }

    /**
     * Retrieves a gift certificate by its ID.
     *
     * @param id a gift certificate ID
     * @return an {@link java.util.Optional} containing the retrieved gift certificate or empty if the gift
     * certificate does not exist
     * @throws IllegalArgumentException if the provided ID is invalid
     * @throws ServiceException         if an error occurred while retrieving the gift certificate from the database
     */
    @Override
    public Optional<GiftCertificate> getGiftCertificateById(Long id)
            throws IllegalArgumentException, ServiceException {
        try {
            validateId(id);
            return giftCertificateDao.getById(id);
        } catch (DbException e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while searching a gift certificate with id =" + id + "; ");
        }
    }

    /**
     * Creates a new gift certificate in the database.
     *
     * @param giftCertificate a {@link com.epam.esm.model.GiftCertificate} object to create
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
            giftCertificateDao.create(giftCertificate);
            addTags(giftCertificate);

            Optional<GiftCertificate> CreatedGiftCertificate = giftCertificateDao.getById(giftCertificate.getId());
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
                Optional<Tag> existingTag = tagDao.getByName(tag.name());
                if (existingTag.isPresent()) {
                    newTag = new Tag(existingTag.get().id(), existingTag.get().name());
                } else {
                    newTag = tagDao.create(tag);
                }
                giftCertificateDao.addTagToCertificate(giftCertificate, newTag);
            }
        }
    }

    /**
     * Updates an existing gift certificate in the database.
     *
     * @param id              the ID of the gift certificate to update
     * @param giftCertificate a {@link com.epam.esm.model.GiftCertificate} object containing the new data
     * @return an {@link java.util.Optional} containing the updated gift certificate or empty if the gift
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
            Optional<GiftCertificate> optionalOldGiftCertificate = giftCertificateDao.getById(id);
            if (optionalOldGiftCertificate.isEmpty()) return Optional.empty();
            GiftCertificate oldGiftCertificate = optionalOldGiftCertificate.get();

            updateCertificate(oldGiftCertificate, giftCertificate);
            giftCertificateDao.update(oldGiftCertificate);

            if (giftCertificate.getTags() != null) {
                validateTags(giftCertificate.getTags());
                giftCertificateDao.deleteAllTagsForCertificateById(oldGiftCertificate.getId());
                addTags(oldGiftCertificate);
            }
            return giftCertificateDao.getById(id);
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
     * @return true if the gift certificate was successfully deleted, false otherwise
     * @throws IllegalArgumentException if the specified id is null or negative
     * @throws ServiceException         if an error occurs while deleting the gift certificate
     */
    @Override
    public boolean deleteGiftCertificate(Long id) throws IllegalArgumentException, ServiceException {
        validateId(id);
        try {
            return giftCertificateDao.delete(id);
        } catch (DbException e) {
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
    public List<GiftCertificate> getGiftCertificates(GiftCertificateFilter giftCertificateFilter, int page, int size, String[] sortParams) throws ServiceException {
        try {
            Optional<Sort> sort = createSort(sortParams, ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);

            List<GiftCertificate> giftCertificates =
                    giftCertificateDao.getAllWithSearchQuery(
                            giftCertificateFilter.getSearchQuery(), sort.orElse(null)
                    );

            return giftCertificateFilter.filter(giftCertificates.stream())
                    .skip((long) page * size)
                    .limit(size)
                    .toList();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            throw new ServiceException(e.getMessage());
        }
    }
}
