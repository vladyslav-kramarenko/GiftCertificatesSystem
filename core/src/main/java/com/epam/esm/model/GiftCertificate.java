package com.epam.esm.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Lazy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class represents a Gift Certificate entity. It contains the following fields:
 * <ul>
 *     <li>id - a unique identifier for the gift certificate</li>
 *     <li>name - the name of the gift certificate</li>
 *     <li>description - a description of the gift certificate</li>
 *     <li>price - the price of the gift certificate</li>
 *     <li>duration - the duration (in days) of the gift certificate's validity</li>
 *     <li>createDate - the creation date of the gift certificate</li>
 *     <li>lastUpdateDate - the last updated date of the gift certificate</li>
 *     <li>tags - a list of associated {@link Tag} objects</li>
 * </ul>
 * <p>
 * The class is annotated with {@link Getter}, {@link Setter}, {@link EqualsAndHashCode},
 * and {@link ToString} to automatically generate getters, setters, equals, hashCode, and toString methods.
 * <p>
 * The {@link LocalDateTime} fields are annotated with {@link JsonSerialize} to specify a custom serializer
 * for serializing the LocalDateTime objects to JSON.
 */
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class GiftCertificate {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer duration;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createDate;
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastUpdateDate;
    @Lazy
    private List<Tag> tags;
}
