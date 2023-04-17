package com.epam.esm.core.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "user_order")
public class UserOrder {
    @Id
    @GeneratedValue
    @Column(name = "id")
    Long id;

    @NotNull(message = "Order Sum cannot be blank")
    @Positive(message = "Order Sum can't be negative")
    private BigDecimal sum;

    @GeneratedValue
    @Column(name = "create_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDate;

    @GeneratedValue
    @Column(name = "last_update_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdateDate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<OrderGiftCertificate> orderGiftCertificates;
}