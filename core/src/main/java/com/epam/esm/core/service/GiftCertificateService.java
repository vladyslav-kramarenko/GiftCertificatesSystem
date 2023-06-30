package com.epam.esm.core.service;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.exception.ServiceException;

import java.util.List;
import java.util.Optional;

/**
 * The interface {@code GiftCertificateService} provides methods
 * to perform CRUD operations and retrieve gift certificates
 * with sorting and filtering options.
 */
public interface GiftCertificateService {

    /**
     * Retrieves a gift certificate by ID.
     *
     * @param id the ID of the gift certificate to retrieve
     * @return an Optional of GiftCertificate, containing the gift certificate or null if not found
     * @throws ServiceException if there was an error performing the operation
     */
    Optional<GiftCertificate> getGiftCertificateById(Long id) throws ServiceException;

    /**
     * Creates a new gift certificate.
     *
     * @param certificate the GiftCertificate object to create
     * @return the created GiftCertificate object
     * @throws ServiceException if there was an error performing the operation
     */
    GiftCertificate createGiftCertificate(GiftCertificate certificate) throws ServiceException;

    /**
     * Updates an existing gift certificate.
     *
     * @param id          the ID of the gift certificate to update
     * @param certificate the new GiftCertificate object to update
     * @return an Optional of GiftCertificate, containing the updated gift certificate or null if not found
     * @throws ServiceException if there was an error performing the operation
     */
    Optional<GiftCertificate> updateGiftCertificate(Long id, GiftCertificate certificate) throws ServiceException;

    /**
     * Deletes a gift certificate by ID.
     *
     * @param id the ID of the gift certificate to delete
     * @throws ServiceException if there was an error performing the operation
     */
    void deleteGiftCertificate(Long id) throws ServiceException;

    /**
     * Retrieves a list of gift certificates, filtered and sorted by the specified parameters.
     *
     * @param searchQuery
     * @param tags
     * @param page        the page number to retrieve
     * @param size        the number of elements per page
     * @param sortParams  an array of strings representing the sorting parameters (field names and directions)
     * @return a list of GiftCertificate objects matching the specified filter and sorted by the specified parameters
     * @throws ServiceException if there was an error performing the operation
     */
    List<GiftCertificate> getGiftCertificates(
            String searchQuery, String[] tags, int page, int size, String[] sortParams) throws ServiceException;

    List<GiftCertificate> searchGiftCertificates(String searchQuery, int page, int size, Integer minPrice, Integer maxPrice, String[] sortParams);
}
