package com.bank.domain;

import lombok.Builder;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
@Builder
public class TransferCompletedEvent {
    private final DateTime timestamp;
    private final double amount;
    private final String sourceAccountId;
    private final String destinationAccountId;
    private final double fee;
}
