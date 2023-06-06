package com.epam.esm.api.util;

import com.epam.esm.api.controller.GiftCertificateController;
import com.epam.esm.api.controller.OrderController;
import com.epam.esm.api.controller.TagController;
import com.epam.esm.api.controller.UserController;
import com.epam.esm.core.exception.ServiceException;
import org.springframework.hateoas.CollectionModel;

import java.util.List;

import static com.epam.esm.api.util.Constants.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class LinksUtils {

    public static void addOrderNavigationLinks(CollectionModel<?> collection, List<?> list, int page, int size, String[] sortParams) throws ServiceException {
        collection.add(linkTo(methodOn(OrderController.class).getOrders(page, size, sortParams)).withSelfRel());
        collection.add(linkTo(methodOn(OrderController.class).getOrders(0, size, sortParams)).withRel(FIRST));
        if (page > 0) {
            collection.add(linkTo(methodOn(OrderController.class).getOrders(page - 1, size, sortParams)).withRel(PREVIOUS));
        }
        if (!list.isEmpty()) {
            collection.add(linkTo(methodOn(OrderController.class).getOrders(page + 1, size, sortParams)).withRel(NEXT));
        }
        collection.add(getCreateOrderLink());
    }

    public static void addUserNavigationLinks(
            CollectionModel<?> collection, List<?> list, int page, int size, String[] sortParams) throws ServiceException {
        collection.add(linkTo(methodOn(UserController.class).getUsers(page, size, sortParams)).withSelfRel());
        collection.add(linkTo(methodOn(UserController.class).getUsers(0, size, sortParams)).withRel(FIRST));
        if (page > 0) {
            collection.add(linkTo(methodOn(UserController.class).getUsers(page - 1, size, sortParams)).withRel(PREVIOUS));
        }
        if (!list.isEmpty()) {
            collection.add(linkTo(methodOn(UserController.class).getUsers(page + 1, size, sortParams)).withRel(NEXT));
        }
        collection.add(getCreateUserLink());
    }

    public static void addOrderNavigationLinks(
            CollectionModel<?> collection, List<?> list, String search, String[] tags, int page, int size, String[] sortParams
    ) throws ServiceException {

        collection.add(
                linkTo(
                        methodOn(GiftCertificateController.class)
                                .getGiftCertificates(search, tags, page, size, sortParams)
                ).withSelfRel());
        collection.add(
                linkTo(
                        methodOn(GiftCertificateController.class)
                                .getGiftCertificates(search, tags, 0, size, sortParams)
                ).withRel(FIRST));
        if (page > 0) {
            collection.add(
                    linkTo(methodOn(GiftCertificateController.class)
                            .getGiftCertificates(search, tags, page - 1, size, sortParams)
                    ).withRel(PREVIOUS));
        }
        if (!list.isEmpty()) {
            collection.add(
                    linkTo(methodOn(GiftCertificateController.class)
                            .getGiftCertificates(search, tags, page + 1, size, sortParams)
                    ).withRel(NEXT));
        }
        collection.add(getCreateGiftCertificateLink());
    }

    public static CustomLink getCreateGiftCertificateLink() throws ServiceException {
        return new CustomLink(linkTo(methodOn(GiftCertificateController.class).createGiftCertificate(null))
                .toUriComponentsBuilder().toUriString(), "create gift certificate", METHOD_POST);
    }

    public static CustomLink getGiftCertificateSelfLink(Long giftCertificateId) throws ServiceException {
        return new CustomLink(linkTo(methodOn(GiftCertificateController.class).
                getGiftCertificateById(giftCertificateId))
                .toUriComponentsBuilder().
                toUriString(), SELF, METHOD_GET);
    }

    public static CustomLink getCreateUserLink() throws ServiceException {
        return new CustomLink(linkTo(methodOn(UserController.class).addUser(null))
                .toUriComponentsBuilder().toUriString(), "CreateUser", METHOD_POST);
    }

    public static CustomLink getCreateTagLink() throws ServiceException {
        return new CustomLink(linkTo(methodOn(TagController.class).addTag(null))
                .toUriComponentsBuilder().toUriString(), "createTag", METHOD_POST);
    }

    public static CustomLink getCreateOrderLink() throws ServiceException {
        return new CustomLink(linkTo(methodOn(OrderController.class).createOrder(null))
                .toUriComponentsBuilder().toUriString(), "createOrder", METHOD_POST);
    }
}
