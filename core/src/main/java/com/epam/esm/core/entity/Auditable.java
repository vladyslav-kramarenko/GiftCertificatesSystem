package com.epam.esm.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class Auditable {
    @Column(name = "create_date")
    protected LocalDateTime createDate;
    @Column(name = "last_update_date")
    protected LocalDateTime lastUpdateDate;

    @PrePersist
    protected void onCreate() {
        createDate = LocalDateTime.now();
        lastUpdateDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdateDate = LocalDateTime.now();
    }
}