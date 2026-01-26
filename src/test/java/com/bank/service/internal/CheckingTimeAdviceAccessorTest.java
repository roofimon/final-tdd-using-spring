package com.bank.service.internal;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bank.service.TimeService;

public class CheckingTimeAdviceAccessorTest {

	@Test
	public void getTimeServiceReturnsInjectedInstance() {
		CheckingTimeAdvice advice = new CheckingTimeAdvice();
		TimeService ts = new com.bank.service.internal.DefaultTimeService(new org.joda.time.LocalTime(9, 0),
				new org.joda.time.LocalTime(17, 0));
		advice.setTimeService(ts);
		assertNotNull(advice.getTimeService());
	}
}
