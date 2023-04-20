package com.epam.esm.core.entity;

import com.epam.esm.core.util.CoreConstants;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
@Table(name = "tag")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;
    @NotNull(message = "Gift Certificate Name cannot be blank")
    @Size(max = CoreConstants.MAX_TAG_NAME_LENGTH,
            message = "Tag Name must be less then " + CoreConstants.MAX_TAG_NAME_LENGTH + " characters")
    @Column(name = "name")
    String name;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "gift_certificate_has_tag",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "gift_certificate_id")
    )
    @JsonBackReference
    List<GiftCertificate> giftCertificates;

    public Tag(Long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Tag() {}
}
