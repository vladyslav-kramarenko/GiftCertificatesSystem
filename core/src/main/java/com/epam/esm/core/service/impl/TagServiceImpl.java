package com.epam.esm.core.service.impl;

import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.exception.ServiceException;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.service.TagService;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.epam.esm.core.util.CoreConstants.ALLOWED_SORT_DIRECTIONS;
import static com.epam.esm.core.util.CoreConstants.ALLOWED_TAG_SORT_FIELDS;
import static com.epam.esm.core.util.SortUtilities.createSort;

/**
 * Implementation of the {@link TagService} interface that provides the business logic for working with tags.
 */
@Service
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);
    private final TagRepository tagRepository;
    private final GiftCertificateRepository giftCertificateRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, GiftCertificateRepository giftCertificateRepository) {
        this.tagRepository = Objects.requireNonNull(tagRepository, "TagRepository must be initialised");
        this.giftCertificateRepository = Objects.requireNonNull(giftCertificateRepository, "GiftCertificateRepository must be initialised");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Tag> getTagById(Long id) throws ServiceException {
        try {
            Optional<Tag> tagOpt = tagRepository.findById(id);
            if (tagOpt.isPresent()) {
                Tag tag = tagOpt.get();
                Hibernate.initialize(tag.getGiftCertificates());
                return Optional.of(tag);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            throw new ServiceException("Error while get tag with id = " + id);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tag createTag(Tag tag) {
        Optional<Tag> existingTag = tagRepository.getByName(tag.getName());
        if (existingTag.isEmpty()) {
            return tagRepository.save(tag);
        }
        return existingTag.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTag(Long id) {
        validateIsThisOnlyTagForSomeCertificate(id);
        tagRepository.deleteById(id);
    }

    private void validateIsThisOnlyTagForSomeCertificate(long id) throws IllegalArgumentException {
        List<GiftCertificate> certificatesWithTag = giftCertificateRepository.getCertificatesByTagId(id);
        boolean isThisOnlyOneTagForSomeCertificate = certificatesWithTag.stream().anyMatch(x -> x.getTags().size() == 1);
        if (isThisOnlyOneTagForSomeCertificate) {
            throw new IllegalArgumentException("Cannot delete the only tag of a certificate");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tag> getTags(int page, int size, String[] sortParams) {
        Optional<Sort> sort = createSort(sortParams, ALLOWED_TAG_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        Pageable pageable = PageRequest.of(page, size, sort.orElse(Sort.by("id").ascending()));
        return tagRepository.findAll(pageable).toList();
    }

    @Override
    public Optional<Tag> getMostWidelyUsedTagWithHighestCostByUserId(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Tag> tags = tagRepository.findMostWidelyUsedTagWithHighestCostByUserId(userId, pageable);
        return tags.stream().findFirst();
    }
}
