package com.bank.service.internal;

import org.joda.time.LocalTime;
import org.junit.Test;

import com.bank.service.TimeService;

import static org.junit.Assert.*;

public class DefaultTimeServiceTest {

    @Test
    public void testIsServiceAvailable() {
        //given
        LocalTime openService = new LocalTime(6, 0);
        LocalTime closeService = new LocalTime(22, 0);
        LocalTime testTime = new LocalTime(9, 0);
        TimeService timeService = new DefaultTimeService(openService, closeService);

        //when
        boolean result = timeService.isServiceAvailable(testTime);

        //then
        assertTrue(result);
    }

    @Test
    public void testIsServiceAvailableWhenOutOfService() {
        //given
        LocalTime openService = new LocalTime(6, 0);
        LocalTime closeService = new LocalTime(22, 0);
        LocalTime testTime = new LocalTime(22, 1);
        TimeService timeService = new DefaultTimeService(openService, closeService);

        //when
        boolean result = timeService.isServiceAvailable(testTime);

        //then
        assertFalse(result);
    }
}
