package com.epam.esm.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class OrderGiftCertificateId implements Serializable {

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "gift_certificate_id")
    private Long giftCertificateId;

    public OrderGiftCertificateId() {
    }

    public OrderGiftCertificateId(Long orderId, Long giftCertificateId) {
        this.orderId = orderId;
        this.giftCertificateId = giftCertificateId;
    }
}
