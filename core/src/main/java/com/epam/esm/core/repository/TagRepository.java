package com.epam.esm.core.repository;


import com.epam.esm.core.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{
    Optional<Tag> getByName(String name);
}