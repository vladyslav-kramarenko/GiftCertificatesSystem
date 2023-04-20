package com.epam.esm.core.filter;

import com.epam.esm.core.util.CoreConstants;

public class GiftCertificateFilter {
    String[] tags;
    String searchQuery;

    public static GiftCertificateFilterBuilder builder() {
        return new GiftCertificateFilterBuilder();
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public String[] getTags() {
        return tags;
    }

    public static class GiftCertificateFilterBuilder {
        String[] tags;
        String searchQuery;

        public GiftCertificateFilterBuilder withTags(String[] tags) {
            if (tags != null) {
                for (String tag : tags) {
                    if (tag.length() > CoreConstants.MAX_TAG_NAME_LENGTH) {
                        throw new IllegalArgumentException("Tag name should be less than " + CoreConstants.MAX_TAG_NAME_LENGTH + " characters");
                    }
                }

            } else tags = new String[0];
            this.tags = tags;
            return this;
        }

        public GiftCertificateFilterBuilder withSearchQuery(String searchQuery) {
            if (searchQuery == null) searchQuery = "";
            this.searchQuery = searchQuery;
            return this;
        }

        public GiftCertificateFilter build() {
            return new GiftCertificateFilter(this);
        }
    }

    private GiftCertificateFilter(GiftCertificateFilterBuilder builder) {
        this.tags = builder.tags;
        this.searchQuery = builder.searchQuery;
    }
}
