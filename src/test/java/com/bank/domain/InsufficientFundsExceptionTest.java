package com.bank.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InsufficientFundsExceptionTest {

	@Test
	public void gettersAndToStringArePopulated() {
		Account a = new Account("A123", 50.00);
		try {
			a.debit(75.00);
		} catch (InsufficientFundsException ex) {
			assertEquals("A123", ex.getTargetAccountId());
			assertEquals(50.00, ex.getTargetAccountBalance(), 0.0001);
			assertEquals(75.00, ex.getAttemptedAmount(), 0.0001);
			assertEquals(25.00, ex.getOverage(), 0.0001);

			String s = ex.toString();
			assertTrue(s.contains("Failed to transfer"));
			assertTrue(s.contains("A123"));
			assertTrue(s.contains("overage"));
		}
	}
}
