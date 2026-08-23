package com.bank.service;

import static java.lang.String.format;

/**
 * Thrown when a requested transfer amount does not meet the configured minimum.
 */
public class InvalidTransferAmountException extends IllegalArgumentException {

	private final double attemptedAmount;
	private final double minimumAmount;

	public InvalidTransferAmountException(double attemptedAmount, double minimumAmount) {
		super(format("Transfer amount must be at least $%.2f.", minimumAmount));
		this.attemptedAmount = attemptedAmount;
		this.minimumAmount = minimumAmount;
	}

	public double getAttemptedAmount() {
		return attemptedAmount;
	}

	public double getMinimumAmount() {
		return minimumAmount;
	}
}
