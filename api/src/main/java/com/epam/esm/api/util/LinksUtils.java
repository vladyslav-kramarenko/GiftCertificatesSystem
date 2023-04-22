package com.epam.esm.api.util;

import com.epam.esm.api.controller.GiftCertificateController;
import com.epam.esm.api.controller.OrderController;
import com.epam.esm.api.controller.TagController;
import com.epam.esm.api.controller.UserController;
import org.springframework.hateoas.CollectionModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class LinksUtils {
    public static void addOrderNavigationLinks(CollectionModel<?> collection, List<?> list, int page, int size, String[] sortParams) {
        collection.add(linkTo(methodOn(OrderController.class).getOrders(page, size, sortParams)).withSelfRel());
        collection.add(linkTo(methodOn(OrderController.class).getOrders(0, size, sortParams)).withRel("first"));
        if (page > 0) {
            collection.add(linkTo(methodOn(OrderController.class).getOrders(page - 1, size, sortParams)).withRel("previous"));
        }
        if (!list.isEmpty()) {
            collection.add(linkTo(methodOn(OrderController.class).getOrders(page + 1, size, sortParams)).withRel("next"));
        }
    }

    public static void addUserNavigationLinks(
            CollectionModel<?> collection, List<?> list, int page, int size, String[] sortParams) {
        collection.add(linkTo(methodOn(UserController.class).getUsers(page, size, sortParams)).withSelfRel());
        collection.add(linkTo(methodOn(UserController.class).getUsers(0, size, sortParams)).withRel("first"));
        if (page > 0) {
            collection.add(linkTo(methodOn(UserController.class).getUsers(page - 1, size, sortParams)).withRel("previous"));
        }
        if (!list.isEmpty()) {
            collection.add(linkTo(methodOn(UserController.class).getUsers(page + 1, size, sortParams)).withRel("next"));
        }
        collection.add(getCreateUserLink());
    }

    public static void addOrderNavigationLinks(
            CollectionModel<?> collection, List<?> list, String search, String[] tags, int page, int size, String[] sortParams
    ) {

        collection.add(
                linkTo(
                        methodOn(GiftCertificateController.class)
                                .getGiftCertificates(search, tags, page, size, sortParams)
                ).withSelfRel());
        collection.add(
                linkTo(
                        methodOn(GiftCertificateController.class)
                                .getGiftCertificates(search, tags, 0, size, sortParams)
                ).withRel("first"));
        if (page > 0) {
            collection.add(
                    linkTo(methodOn(GiftCertificateController.class)
                            .getGiftCertificates(search, tags, page - 1, size, sortParams)
                    ).withRel("previous"));
        }
        if (!list.isEmpty()) {
            collection.add(
                    linkTo(methodOn(GiftCertificateController.class)
                            .getGiftCertificates(search, tags, page + 1, size, sortParams)
                    ).withRel("next"));
        }
        collection.add(getCreateGiftCertificateLink());
    }

    public static CustomLink getCreateGiftCertificateLink() {
        return new CustomLink(linkTo(methodOn(GiftCertificateController.class).createGiftCertificate(null))
                .toUriComponentsBuilder().toUriString(), "create gift certificate", "POST");
    }

    public static CustomLink getGiftCertificateSelfLink(Long giftCertificateId) {
        return new CustomLink(linkTo(methodOn(GiftCertificateController.class).
                getGiftCertificateById(giftCertificateId))
                .toUriComponentsBuilder().
                toUriString(), "self", "GET");
    }

    public static CustomLink getCreateUserLink() {
        return new CustomLink(linkTo(methodOn(UserController.class).addUser(null))
                .toUriComponentsBuilder().toUriString(), "CreateUser", "POST");
    }

    public static CustomLink getCreateTagLink() {
        return new CustomLink(linkTo(methodOn(TagController.class).addTag(null))
                .toUriComponentsBuilder().toUriString(), "createTag", "POST");
    }
}
