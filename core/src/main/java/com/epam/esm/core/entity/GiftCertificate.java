package com.epam.esm.core.entity;

import com.epam.esm.core.util.CoreConstants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "search_gift_certificates_inout", procedureName = "search_gift_certificates_sort",
                resultClasses = GiftCertificate.class,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "search_term", type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, name = "sort_conditions", type = String.class)
                })
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "gift_certificate")
public class GiftCertificate {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull(message = "Gift Certificate Name cannot be blank")
    @Size(max = CoreConstants.MAX_GIFT_CERTIFICATE_NAME_LENGTH,
            message = "Gift Certificate Name must be less then " + CoreConstants.MAX_GIFT_CERTIFICATE_NAME_LENGTH + " characters")
    private String name;

    @NotNull(message = "Gift Certificate Description cannot be blank")
    @Size(max = CoreConstants.MAX_GIFT_CERTIFICATE_NAME_LENGTH,
            message = "Gift Certificate Description must be less then " + CoreConstants.MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH + " characters")
    private String description;

    @NotNull(message = "Gift Certificate Price cannot be blank")
    @Positive(message = "Gift Certificate Price can't be negative")
    private BigDecimal price;

    @NotNull(message = "Gift Certificate Duration cannot be blank")
    @Positive(message = "Gift Certificate Duration can't be negative")
    private Integer duration;

    @GeneratedValue
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDate;

    @GeneratedValue
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdateDate;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "giftCertificates")
    @JsonManagedReference
    private List<Tag> tags;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "order_has_gift_certificate",
            joinColumns = @JoinColumn(name = "gift_certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    @JsonBackReference
    private List<UserOrder> orders;
}
