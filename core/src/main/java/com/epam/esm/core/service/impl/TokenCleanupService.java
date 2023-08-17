package com.epam.esm.core.service.impl;

import com.epam.esm.core.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenCleanupService {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    // This method will be invoked every day at midnight.
    // seconds, minutes, hours, day of month, month, day of week, year(optional)
    //  * (all values)
    //  - (range of values)
    //  , (additional values)
    //  / (increments)
    //  ? (no specific value)
    //  L ('L' stands for 'last'. When used in the day-of-week field, it allows you to specify constructs such as 'the last Friday' ('5L') of a given month. In the day-of-month field, it specifies the last day of the month.)
    //  W - weekday (Monday-Friday) nearest the given day
    //  # ('#' is used to specify 'the nth' XXX day of the month)
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupExpiredTokens() {
        refreshTokenRepository.deleteAllExpiredTokens(Instant.now());
    }
}
