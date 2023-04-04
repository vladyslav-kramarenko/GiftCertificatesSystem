package com.epam.esm.service.impl;

import com.epam.esm.dao.TagDao;
import com.epam.esm.exception.DbException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.Tag;
import com.epam.esm.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.epam.esm.util.Constants.ALLOWED_SORT_DIRECTIONS;
import static com.epam.esm.util.Constants.ALLOWED_TAG_SORT_FIELDS;
import static com.epam.esm.util.SortUtilities.createSort;
import static com.epam.esm.util.TagUtils.validateTag;
import static com.epam.esm.util.Utilities.validateId;

@Service
public class TagServiceImpl implements TagService {
    private final TagDao tagDao;

    @Autowired
    public TagServiceImpl(TagDao tagDao) {
        this.tagDao = tagDao;
    }

    @Override
    public Optional<Tag> getTagById(Long id) throws ServiceException {
        validateId(id);
        try {
            return tagDao.getById(id);
        } catch (DbException e) {
            throw new ServiceException("Error while get tag with id = " + id);
        }
    }

    @Override
    public Tag createTag(Tag tag) throws ServiceException {
        validateTag(tag);
        try {
            return tagDao.create(tag);
        } catch (DbException e) {
            throw new ServiceException("Error while creating a tag");
        }
    }

    @Override
    public boolean deleteTag(Long id) throws ServiceException {
        validateId(id);
        try {
            return tagDao.delete(id);
        } catch (DbException e) {
            throw new ServiceException("Error while deleting tag with id = " + id);
        }
    }

    @Override
    public List<Tag> getTags(String[] sortParams) throws ServiceException {
        Optional<Sort> sort = createSort(sortParams, ALLOWED_TAG_SORT_FIELDS, ALLOWED_SORT_DIRECTIONS);
        try {
            return tagDao.getAll(sort.orElse(null));
        } catch (DbException e) {
            throw new ServiceException("Error while getting all tags");
        }
    }
}
