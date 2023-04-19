package com.epam.esm.core.repository;


import com.epam.esm.core.entity.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> getByName(String name);

    @Query("""
            SELECT t 
            FROM Tag t 
            JOIN t.giftCertificates gc 
            JOIN gc.orders o 
            WHERE o.user.id = :userId 
            GROUP BY t.id 
            ORDER BY COUNT(t) DESC, SUM(o.sum) DESC""")
    List<Tag> findMostWidelyUsedTagWithHighestCostByUserId(@Param("userId") Long userId, Pageable pageable);

}