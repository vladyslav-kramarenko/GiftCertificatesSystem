package com.epam.esm.core.entity;

import com.epam.esm.core.util.CoreConstants;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
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
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @Column(unique = true)
    String auth0UserId;

    @Column(name = "password")
    String password;

    @NotNull(message = "Gift Certificate Name cannot be blank")
    @NotEmpty(message = "Gift Certificate Name cannot be blank")
    @Size(max = CoreConstants.MAX_USER_FIRST_NAME_LENGTH,
            message = "Tag Name must be less then " + CoreConstants.MAX_TAG_NAME_LENGTH + " characters")
    @Column(name = "first_name", nullable = false)
    String firstName;
    @NotNull(message = "Gift Certificate Name cannot be blank")
    @NotEmpty(message = "Gift Certificate Name cannot be blank")
    @Size(max = CoreConstants.MAX_USER_LAST_NAME_LENGTH,
            message = "Tag Name must be less then " + CoreConstants.MAX_TAG_NAME_LENGTH + " characters")
    @Column(name = "last_name")
    String lastName;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    List<UserOrder> orders;

    @NotNull(message = "Email cannot be blank")
    @NotEmpty(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Size(max = 64, message = "Email must be less than 64 characters")
    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "is_admin", nullable = false)
    boolean isAdmin;
}
