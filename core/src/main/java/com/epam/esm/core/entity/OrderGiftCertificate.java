package com.epam.esm.core.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "order_has_gift_certificate")
@Data
public class OrderGiftCertificate {
    @EmbeddedId
    private OrderGiftCertificateId id;

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    private UserOrder order;

    @ManyToOne
    @MapsId("giftCertificateId")
    @JoinColumn(name = "gift_certificate_id")
    private GiftCertificate giftCertificate;

    @Column(name = "count", nullable = false)
    private int count;
}
