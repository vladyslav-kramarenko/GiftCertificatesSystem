package com.epam.esm.dao;

import com.epam.esm.exception.DbException;
import com.epam.esm.model.GiftCertificate;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

/**
 * This interface provides the methods for performing CRUD operations on GiftCertificates in the database.
 */
public interface GiftCertificateDao {
    /**
     * Creates a new GiftCertificate in the database.
     *
     * @param giftCertificate the GiftCertificate object to be created
     * @return the created GiftCertificate object
     * @throws DbException if an error occurs while accessing the database
     */
    GiftCertificate create(GiftCertificate giftCertificate) throws DbException;

    /**
     * Retrieves a GiftCertificate with the specified ID from the database.
     *
     * @param id the ID of the GiftCertificate to retrieve
     * @return an Optional containing the retrieved GiftCertificate object, or an empty Optional if the GiftCertificate was not found
     * @throws DbException if an error occurs while accessing the database
     */
    Optional<GiftCertificate> getById(Long id) throws DbException;

    /**
     * Retrieves all GiftCertificates from the database.
     *
     * @return a List containing all the GiftCertificate objects
     * @throws DbException if an error occurs while accessing the database
     */
    List<GiftCertificate> getAll() throws DbException;

    /**
     * Retrieves all GiftCertificates from the database, sorted as specified by the Sort object.
     *
     * @param sort the Sort object that specifies the sorting criteria
     * @return a List containing all the GiftCertificate objects, sorted as specified by the Sort object
     * @throws DbException if an error occurs while accessing the database
     */
    List<GiftCertificate> getAll(Sort sort) throws DbException;

    /**
     * Retrieves all GiftCertificates from the database that match the specified search query, sorted as specified by the Sort object.
     *
     * @param searchQuery the search query to match against the GiftCertificates' names and descriptions
     * @param sort        the Sort object that specifies the sorting criteria
     * @return a List containing all the matching GiftCertificate objects, sorted as specified by the Sort object
     * @throws DbException if an error occurs while accessing the database
     */
    List<GiftCertificate> getAllWithSearchQuery(String searchQuery, Sort sort) throws DbException;

    /**
     * Updates an existing GiftCertificate in the database.
     *
     * @param giftCertificate the GiftCertificate object to update
     * @return the updated GiftCertificate object
     * @throws DbException if an error occurs while accessing the database
     */
    GiftCertificate update(GiftCertificate giftCertificate) throws DbException;

    /**
     * Deletes a GiftCertificate with the specified ID from the database.
     *
     * @param id the ID of the GiftCertificate to delete
     * @return true if the GiftCertificate was deleted, false otherwise
     * @throws DbException if an error occurs while accessing the database
     */
    boolean delete(Long id) throws DbException;

    List<GiftCertificate> getCertificatesByTagId(Long tagId);
}