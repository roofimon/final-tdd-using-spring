package com.bank.domain;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TransferReceiptTest {

	@Test
	public void toStringContainsKeyDetails() {
		Account srcInitial = new Account("A123", 100.00);
		Account destInitial = new Account("C456", 0.00);

		TransferReceipt receipt = new TransferReceipt();
		receipt.setTransferAmount(75.00);
		receipt.setFeeAmount(5.00);
		receipt.setInitialSourceAccount(srcInitial);
		receipt.setInitialDestinationAccount(destInitial);

		// simulate final balances
		Account srcFinal = new Account("A123", 20.00);
		Account destFinal = new Account("C456", 75.00);
		receipt.setFinalSourceAccount(srcFinal);
		receipt.setFinalDestinationAccount(destFinal);

		String s = receipt.toString();
		assertTrue(s.contains("Transferred"));
		assertTrue(s.contains("A123"));
		assertTrue(s.contains("C456"));
		assertTrue(s.contains("fee amount"));
		assertTrue(s.contains("initial balance"));
		assertTrue(s.contains("new balance"));
	}
}
