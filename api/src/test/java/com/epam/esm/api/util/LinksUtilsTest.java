package com.epam.esm.api.util;

import com.epam.esm.core.exception.ServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static com.epam.esm.api.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class LinksUtilsTest {

    private static final String[] SORT_PARAMS = new String[]{"param1", "param2"};
    private List<String> list;
    private String[] sortParams;
    private int page;
    private int size;
    CollectionModel<String> collectionModel;
    @BeforeEach
    public void setUp() {
        list = Arrays.asList("Test1", "Test2", "Test3");
        collectionModel = CollectionModel.of(list);
        sortParams = new String[]{"param1", "param2"};
        page = 1;
        size = 10;
    }

    @Test
    public void testAddUserNavigationLinks() throws ServiceException {
        LinksUtils.addUserNavigationLinks(collectionModel, list, 1, 10, SORT_PARAMS);

        List<Link> links = collectionModel.getLinks().toList();
        assertEquals(5, links.size()); // 5 links expected (self, first, previous, next, create user)
        checkGeneralNavigationLinks(links);
        assertTrue(links.stream().anyMatch(link -> link.getRel().value().equals("CreateUser")));
    }

    @Test
    public void testAddUserNavigationLinksWithTags() throws ServiceException {
        LinksUtils.addUserNavigationLinks(collectionModel, list, page, size, sortParams);
        List<Link> links = collectionModel.getLinks().toList();
        checkPreviousLink(links);
        checkNextLink(links);
        assertTrue(collectionModel.hasLink("CreateUser"));
    }

    private void checkPreviousLink(List<Link> links) {
        assertTrue(links.stream().anyMatch(link -> link.getRel().value().equals(PREVIOUS)));
    }

    private void checkNextLink(List<Link> links) {
        assertTrue(links.stream().anyMatch(link -> link.getRel().value().equals(NEXT)));
    }

    private void checkGeneralNavigationLinks(List<Link> links) {
        assertTrue(links.stream().anyMatch(link -> link.getRel().value().equals(SELF)));
        assertTrue(links.stream().anyMatch(link -> link.getRel().value().equals(FIRST)));
        checkPreviousLink(links);
        checkNextLink(links);
    }

    @Test
    public void testAddOrderNavigationLinks() throws ServiceException {
        LinksUtils.addOrderNavigationLinks(collectionModel, list, 1, 10, SORT_PARAMS);

        List<Link> links = collectionModel.getLinks().toList();
        assertEquals(5, links.size()); // 5 links expected (self, first, previous, next, create order)
        checkGeneralNavigationLinks(links);
        assertTrue(links.stream().anyMatch(link -> link.getRel().value().equals("createOrder")));
    }
}
