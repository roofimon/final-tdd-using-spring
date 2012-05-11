package com.bank.service.internal;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalTime;

import com.bank.service.OutOfServiceException;
import com.bank.service.TimeService;

public class CheckingTimeAdvice implements MethodInterceptor {
	private static Log log = LogFactory.getLog(CheckingTimeAdvice.class);
	private TimeService timeService;

	public TimeService getTimeService() {
		return timeService;
	}

	public void setTimeService(TimeService timeService) {
		this.timeService = timeService;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		log.info("Checking Time Service");
		if (timeService.isServiceAvailable(new LocalTime())) {
			return invocation.proceed();
		} else {
			throw new OutOfServiceException();
		}
	}

}
