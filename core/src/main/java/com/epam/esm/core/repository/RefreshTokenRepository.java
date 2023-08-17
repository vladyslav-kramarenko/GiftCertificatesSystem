package com.epam.esm.core.repository;

import com.epam.esm.core.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findAll();

    List<RefreshToken> findByUserId(Long userId);

    void deleteByToken(String token);

    @Query("delete from RefreshToken t where t.expiryDate <= :now")
    @Modifying
    void deleteAllExpiredTokens(@Param("now") Instant now);
}