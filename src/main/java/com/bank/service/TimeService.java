package com.bank.service;

import org.joda.time.LocalTime;

public interface TimeService {

	boolean isServiceAvailable(LocalTime testTime);

}
