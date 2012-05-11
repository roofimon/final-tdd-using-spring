package com.bank.service.internal;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aopalliance.intercept.MethodInvocation;
import org.joda.time.LocalTime;
import org.junit.Test;

import com.bank.service.OutOfServiceException;
import com.bank.service.TimeService;

public class CheckingTimeAdviceTest {
	
	@Test
	public void testInvoke() throws Throwable {
		//given
		CheckingTimeAdvice advice = new CheckingTimeAdvice();
		MethodInvocation invocation = mock(MethodInvocation.class);
		TimeService timeService = mock(TimeService.class);
		when(timeService.isServiceAvailable(any(LocalTime.class))).thenReturn(true);
		advice.setTimeService(timeService);
		
		//when
		Object result = advice.invoke(invocation);
		
		//then
		assertNull(result);
		verify(timeService).isServiceAvailable(any(LocalTime.class));
	}

	@Test
	public void testInvokeWithTimeOutOfService() throws Throwable {
		//given
		CheckingTimeAdvice advice = new CheckingTimeAdvice();
		MethodInvocation invocation = mock(MethodInvocation.class);
		TimeService timeService = mock(TimeService.class);
		when(timeService.isServiceAvailable(any(LocalTime.class))).thenReturn(false);
		advice.setTimeService(timeService);
		
		//when
		try {
			advice.invoke(invocation);
			fail();
		} catch (OutOfServiceException e) {
			
			//then
			verify(timeService).isServiceAvailable(any(LocalTime.class));
		}
	}
}
