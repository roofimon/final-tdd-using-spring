package com.bank.service.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class FlatFeePolicyTest {

	@Test
	public void testFlatFeePolicy() {
		FlatFeePolicy feePolicy = new FlatFeePolicy(5.00);

		assertThat(feePolicy.calculateFee(1000), equalTo(5.00));
		assertThat(feePolicy.calculateFee(10), equalTo(5.00));
		assertThat(feePolicy.calculateFee(1), equalTo(5.00));
	}
}
