package com.epam.esm.core.repository.specs;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

public class DistinctSpecification<T> implements Specification<T> {
    private final Specification<T> spec;

    public DistinctSpecification(Specification<T> spec) {
        this.spec = spec;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        query.distinct(true);
        return spec.toPredicate(root, query, criteriaBuilder);
    }
}
