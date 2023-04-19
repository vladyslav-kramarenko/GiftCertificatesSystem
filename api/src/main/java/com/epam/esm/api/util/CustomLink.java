package com.epam.esm.api.util;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;

public class CustomLink extends Link {
    private final String method;

    public CustomLink(String href, String rel, String method) {
        super(href, LinkRelation.of(rel));
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
