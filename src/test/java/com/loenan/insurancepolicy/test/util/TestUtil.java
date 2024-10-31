package com.loenan.insurancepolicy.test.util;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class TestUtil {
    public static void assertDateTimeIsCloseToNow(ZonedDateTime dateTime) {
        assertThat(dateTime)
            .isCloseTo(ZonedDateTime.now(), within(10, ChronoUnit.SECONDS));
    }
}
