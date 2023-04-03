package com.epam.esm.service.impl;

import com.epam.esm.dao.GiftCertificateDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.GiftCertificate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.util.Constants.*;
import static com.epam.esm.util.Utilities.validateId;

@Service
public class GiftCertificateServiceImpl implements com.epam.esm.service.GiftCertificateService {
    private final GiftCertificateDao giftCertificateDao;

    @Autowired
    public GiftCertificateServiceImpl(GiftCertificateDao giftCertificateDao) {
        this.giftCertificateDao = giftCertificateDao;
    }

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

    private void validateForNull(Object parameter, String parameterName) throws IllegalArgumentException {
        if (parameter == null)
            throw new IllegalArgumentException("Gift Certificate " + parameterName + " cannot be empty");
    }

    private void validateGiftCertificateDescription(String description) {
        if (description.length() == 0) {
            throw new IllegalArgumentException("Gift Certificate description cannot be empty");
        }
        if (description.length() > MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH) {
            throw new IllegalArgumentException(
                    "Gift Certificate description should be less than " +
                            MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH +
                            " symbols");
        }
    }

    private void validateGiftCertificateName(String name) {
        if (name.length() == 0) {
            throw new IllegalArgumentException("Gift Certificate name cannot be empty");
        }
        if (name.length() > MAX_GIFT_CERTIFICATE_NAME_LENGTH) {
            throw new IllegalArgumentException("Gift Certificate name should be less than " +
                    MAX_GIFT_CERTIFICATE_NAME_LENGTH +
                    " symbols");
        }
    }

    private void validateGiftCertificatePrice(BigDecimal price) {
        if (price.doubleValue() < 0) {
            throw new IllegalArgumentException("Gift Certificate price cannot be negative");
        }
    }

    private void validateGiftCertificateDuration(Integer duration) {
        if (duration < 0) {
            throw new IllegalArgumentException("Gift Certificate duration cannot be negative");
        }
    }

    private void updateCertificate(GiftCertificate giftCertificateToUpdate, GiftCertificate giftCertificateWithNewData) {
        if (giftCertificateWithNewData.getDescription() != null) {
            validateGiftCertificateDescription(giftCertificateWithNewData.getDescription());
            giftCertificateToUpdate.setDescription(giftCertificateWithNewData.getDescription());
        }
        if (giftCertificateWithNewData.getName() != null) {
            validateGiftCertificateName(giftCertificateWithNewData.getName());
            giftCertificateToUpdate.setName(giftCertificateWithNewData.getDescription());
        }
        if (giftCertificateWithNewData.getDuration() != null) {
            validateGiftCertificateDuration(giftCertificateWithNewData.getDuration());
            giftCertificateToUpdate.setDuration(giftCertificateWithNewData.getDuration());
        }
        if (giftCertificateWithNewData.getPrice() != null) {
            validateGiftCertificatePrice(giftCertificateWithNewData.getPrice());
            giftCertificateToUpdate.setPrice(giftCertificateWithNewData.getPrice());
        }
        if (giftCertificateWithNewData.getTags() != null) {
            giftCertificateToUpdate.setTags(giftCertificateWithNewData.getTags());
        }
    }

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

    public boolean deleteGiftCertificate(Long id) throws IllegalArgumentException, ServiceException {
        validateId(id);
        try {
            return giftCertificateDao.delete(id);
        } catch (DbException e) {
            throw new ServiceException("Error while deleting a gift certificate");
        }
    }

    public List<GiftCertificate> getGiftCertificates() throws ServiceException {
        try {
            return giftCertificateDao.getAll();
        } catch (DbException e) {
            throw new ServiceException("Error while searching for gift certificates");
        }
    }
}
