package com.epam.esm.filter;

import com.epam.esm.model.GiftCertificate;
import com.epam.esm.model.Tag;
import com.epam.esm.util.Constants;

import java.util.stream.Stream;

public class GiftCertificateFilter {
    String tagName;
    String searchQuery;

    public static GiftCertificateFilterBuilder builder() {
        return new GiftCertificateFilterBuilder();
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public static class GiftCertificateFilterBuilder {
        String tagName;
        String searchQuery;

        public GiftCertificateFilterBuilder withTagName(String tagName) {
            if (tagName != null && tagName.length() > Constants.MAX_TAG_NAME_LENGTH) {
                throw new IllegalArgumentException("Tag name should be less than " + Constants.MAX_TAG_NAME_LENGTH + " characters");
            }
            this.tagName = tagName;
            return this;
        }

        public GiftCertificateFilterBuilder withSearchQuery(String searchQuery) {
            this.searchQuery = searchQuery;
            return this;
        }

        public GiftCertificateFilter build() {
            return new GiftCertificateFilter(this);
        }
    }

    private GiftCertificateFilter(GiftCertificateFilterBuilder builder) {
        this.tagName = builder.tagName;
        this.searchQuery = builder.searchQuery;
    }

    public Stream<GiftCertificate> filter(Stream<GiftCertificate> input) {
        return input
                .filter(movie -> (tagName == null ||
                        movie.getTags().stream()
                                .map(Tag::getName)
                                .anyMatch(tag -> tag.equalsIgnoreCase(tagName))));
    }
}
