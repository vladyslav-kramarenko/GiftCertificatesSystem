package com.epam.esm.api.util;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;

public class CustomSQLFilter extends TurboFilter {

    private static final String INSERT_WITH_QUESTION_MARK_REGEX = ".*insert into .*\\([^?]*\\).*\\?";
    private static final String COLLECTION_FOUND_REGEX = ".*Collection found: .*\\(initialized\\)";

    @Override
    public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format,
                              final Object[] params, final Throwable t) {
        if (format != null &&
                (
                        format.matches(INSERT_WITH_QUESTION_MARK_REGEX) ||
                                format.matches(COLLECTION_FOUND_REGEX)
                )
        ) {
            return FilterReply.DENY;
        }
        return FilterReply.NEUTRAL;
    }
}