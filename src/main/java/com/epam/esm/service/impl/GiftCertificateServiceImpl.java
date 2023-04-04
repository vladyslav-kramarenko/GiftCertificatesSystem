package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.filter.GiftCertificateFilter;
import com.epam.esm.model.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.epam.esm.util.Constants.*;
import static com.epam.esm.util.GiftCertificateUtils.*;
import static com.epam.esm.util.Utilities.createSort;
import static com.epam.esm.util.Utilities.validateId;

@Service
public class GiftCertificateServiceImpl implements com.epam.esm.service.GiftCertificateService {
    private final GiftCertificateDao giftCertificateDao;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao giftCertificateDao) {
        this.giftCertificateDao = giftCertificateDao;
    }

    @Override
    public Optional<GiftCertificate> getGiftCertificateById(Long id) throws ServiceException {
        try {
            validateId(id);
            return giftCertificateDao.getById(id);
        } catch (DbException e) {
            throw new ServiceException("Error while searching a gift certificate with id =" + id + "; " + e.getMessage());
        }
    }

    @Override
    public GiftCertificate createGiftCertificate(GiftCertificate giftCertificate) throws IllegalArgumentException, ServiceException {
        validateForNull(giftCertificate.getDescription(), "description");
        validateForNull(giftCertificate.getName(), "name");
        validateForNull(giftCertificate.getPrice(), "price");
        validateForNull(giftCertificate.getDuration(), "duration");

        validateGiftCertificateDescription(giftCertificate.getDescription());
        validateGiftCertificateName(giftCertificate.getName());
        validateGiftCertificatePrice(giftCertificate.getPrice());
        validateGiftCertificateDuration(giftCertificate.getDuration());

        try {
            return giftCertificateDao.create(giftCertificate);
        } catch (DbException e) {
            throw new ServiceException("Error while creating a gift certificate");
        }
    }

    @Override
    public Optional<GiftCertificate> updateGiftCertificate(Long id, GiftCertificate giftCertificate) throws IllegalArgumentException, ServiceException {
        validateId(id);
        try {
            Optional<GiftCertificate> optionalOldGiftCertificate = giftCertificateDao.getById(id);
            if (optionalOldGiftCertificate.isEmpty()) return Optional.empty();
            GiftCertificate oldGiftCertificate = optionalOldGiftCertificate.get();
            updateCertificate(oldGiftCertificate, giftCertificate);
            return Optional.of(giftCertificateDao.update(oldGiftCertificate));
        } catch (DbException e) {
            throw new ServiceException("Error while updating a gift certificate");
        }
    }

    @Override
    public boolean deleteGiftCertificate(Long id) throws IllegalArgumentException, ServiceException {
        validateId(id);
        try {
            return giftCertificateDao.delete(id);
        } catch (DbException e) {
            throw new ServiceException("Error while deleting a gift certificate");
        }
    }

    @Override
    public List<GiftCertificate> getGiftCertificates(GiftCertificateFilter giftCertificateFilter, int page, int size, String[] sortParams) throws ServiceException {
        try {
            Sort sort = createSort(sortParams, ALLOWED_GIFT_CERTIFICATE_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS).get();
            List<GiftCertificate> giftCertificates = giftCertificateDao.getAllWithSearchQuery(giftCertificateFilter.getSearchQuery(), sort);
            return giftCertificateFilter.filter(giftCertificates.stream())
                    .skip((long) page * size)
                    .limit(size)
                    .collect(Collectors.toList());
        } catch (DbException e) {
            throw new ServiceException("Error while searching for gift certificates");
        }
    }
}
