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
    Optional<Tag> getByName(@Param("name") String name);

    @Query("""
            SELECT t 
            FROM Tag t 
            JOIN t.giftCertificates gc 
            JOIN gc.orders o 
            Join o.orderGiftCertificates ogc
            WHERE o.user.id = :userId 
            GROUP BY t.id 
            ORDER BY COUNT(t) DESC, SUM(gc.price*ogc.count) DESC""")
    List<Tag> findMostWidelyUsedTagWithHighestCostByUserId(
            @Param("userId") Long userId,
            Pageable pageable
    );

    @Query(value = """
            SELECT o.user_id, t.id, t.name, COUNT(t.id), SUM(ogc.count * gc.price)
            FROM tag t 
            JOIN gift_certificate_has_tag gct ON t.id = gct.tag_id
            JOIN gift_certificate gc ON gct.gift_certificate_id = gc.id
            JOIN order_has_gift_certificate ogc ON ogc.gift_certificate_id = gc.id
            JOIN user_order o ON o.id = ogc.order_id
            WHERE o.user_id = :userId
            GROUP BY t.id 
            ORDER BY 
                COUNT(t.id) DESC, 
                SUM(ogc.count * gc.price) DESC""",
            nativeQuery = true
    )
    List<Object[]> findMostWidelyUsedTagWithHighestCostByUserIdExtended(
            @Param("userId") Long userId, Pageable pageable);
}