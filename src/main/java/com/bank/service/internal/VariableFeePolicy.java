package com.bank.service.internal;

import com.bank.service.FeePolicy;

public class VariableFeePolicy implements FeePolicy {
	private double maxFreeFee, maxPercentFee, percentage, flatRate;
	
	
	public VariableFeePolicy(double maxFreeFee, double maxPercentFee, double percentage, double flatRate) {
		this.maxFreeFee = maxFreeFee;
		this.maxPercentFee = maxPercentFee;
		this.percentage = percentage;
		this.flatRate = flatRate;
	}

	@Override
	public double calculateFee(double transferAmount) {
		if (transferAmount <= maxFreeFee) {
			return 0.00;
		} else if (transferAmount <= maxPercentFee) {
	        return (transferAmount*percentage)/100;
		} else {
			return flatRate;
		}
	}
}
