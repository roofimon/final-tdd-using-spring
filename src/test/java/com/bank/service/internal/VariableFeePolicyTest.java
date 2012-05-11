package com.bank.service.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class VariableFeePolicyTest {
	
	@Test
	public void testVariableFeePolicy() {
		VariableFeePolicy feePolicy = new VariableFeePolicy(1000d, 1000000d, 1d, 20000d);
		//1,000,001 up flat rate 2,0000
		assertThat(feePolicy.calculateFee(1000001d), equalTo(20000d));
		//1,001 - 1,000,000 percent 1%
		assertThat(feePolicy.calculateFee(1000000d), equalTo(10000d));
		assertThat(feePolicy.calculateFee(1001d), equalTo(10.01d));
		//1000 down free 0
		assertThat(feePolicy.calculateFee(1000d), equalTo(0d));
	}

}
