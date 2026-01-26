package com.bank.domain;

import static org.junit.Assert.assertThrows;

import org.junit.Test;

public class AccountInvalidAmountsTest {

	@Test
	public void creditZeroThrows() {
		Account a = new Account("A123", 10.0);
		assertThrows(IllegalArgumentException.class, () -> a.credit(0.0));
	}

	@Test
	public void debitNegativeThrows() {
		Account a = new Account("A123", 10.0);
		assertThrows(IllegalArgumentException.class, () -> {
			try {
				a.debit(-1.0);
			} catch (com.bank.domain.InsufficientFundsException e) {
				// not expected; assertValid should throw before IOF
				throw new RuntimeException(e);
			}
		});
	}
}
