package com.epam.esm.service;

import com.epam.esm.exception.ServiceException;
import com.epam.esm.model.Tag;

import java.util.List;
import java.util.Optional;

public interface TagService {
    Optional<Tag> getTagById(Long id) throws ServiceException;

    Tag createTag(Tag tag) throws ServiceException;

    boolean deleteTag(Long id) throws ServiceException;

    List<Tag> getTags(String[] sortParams) throws ServiceException;
}
