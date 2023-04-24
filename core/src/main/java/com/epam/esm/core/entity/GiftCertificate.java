package com.epam.esm.core.entity;

import com.epam.esm.core.util.CoreConstants;
import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@NamedStoredProcedureQueries({
        @NamedStoredProcedureQuery(name = "search_gift_certificates_with_tags", procedureName = "search_gift_certificates_with_tags",
                resultClasses = GiftCertificate.class,
                parameters = {
                        @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, type = Integer.class),
                        @StoredProcedureParameter(mode = ParameterMode.IN, type = String.class)
                })
})
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gift_certificate")
public class GiftCertificate extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Gift Certificate Name cannot be blank")
    @NotEmpty(message = "Gift Certificate Name cannot be blank")
    @Size(max = CoreConstants.MAX_GIFT_CERTIFICATE_NAME_LENGTH,
            message = "Gift Certificate Name must be less then " + CoreConstants.MAX_GIFT_CERTIFICATE_NAME_LENGTH + " characters")
    private String name;

    @NotNull(message = "Gift Certificate Description cannot be blank")
    @NotEmpty(message = "Gift Certificate Description cannot be blank")
    @Size(max = CoreConstants.MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH,
            message = "Gift Certificate Description must be less then " + CoreConstants.MAX_GIFT_CERTIFICATE_DESCRIPTION_LENGTH + " characters")
    private String description;

    @NotNull(message = "Gift Certificate Price cannot be blank")
    @Min(value = 0, message = "Gift Certificate Price can't be negative")
    private BigDecimal price;

    @NotNull(message = "Gift Certificate Duration cannot be blank")
    @Min(value = 0, message = "Gift Certificate Duration can't be negative")
    private Integer duration;

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "giftCertificates")
    private List<Tag> tags;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "order_has_gift_certificate",
            joinColumns = @JoinColumn(name = "gift_certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id")
    )
    private List<UserOrder> orders;
}
