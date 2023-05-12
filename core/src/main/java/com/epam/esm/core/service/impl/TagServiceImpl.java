package com.epam.esm.core.service.impl;

import com.epam.esm.core.dto.MostUsedTagDTO;
import com.epam.esm.core.entity.GiftCertificate;
import com.epam.esm.core.entity.Tag;
import com.epam.esm.core.repository.GiftCertificateRepository;
import com.epam.esm.core.repository.TagRepository;
import com.epam.esm.core.service.TagService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

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
    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
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
        throw new IllegalArgumentException("Tag with such name already exists");
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

    @Override
    public List<MostUsedTagDTO> getMostWidelyUsedTagWithHighestCostByUserIdExtended(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return convertToMostUsedTagDTOList(
                tagRepository.findMostWidelyUsedTagWithHighestCostByUserIdExtended(userId, pageable)
        );
    }

    private List<MostUsedTagDTO> convertToMostUsedTagDTOList(List<Object[]> objectList) {
        List<MostUsedTagDTO> dtoList = new ArrayList<>();
        for (Object[] object : objectList) {
            Long userId = ((Integer) object[0]).longValue();
            Long tagId = ((Integer) object[1]).longValue();
            String tagName = (String) object[2];
            Long count = ((Long) object[3]);
            BigDecimal sum = (BigDecimal) object[4];

            MostUsedTagDTO dto = new MostUsedTagDTO(userId, tagId, tagName, count, sum);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
