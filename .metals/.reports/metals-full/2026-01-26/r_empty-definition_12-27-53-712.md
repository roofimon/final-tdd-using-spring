error id: file://<WORKSPACE>/src/test/java/com/bank/service/internal/FlatFeePolicyTest.java:org/hamcrest/MatcherAssert#assertThat().
file://<WORKSPACE>/src/test/java/com/bank/service/internal/FlatFeePolicyTest.java
empty definition using pc, found symbol in pc: org/hamcrest/MatcherAssert#assertThat().
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 126
uri: file://<WORKSPACE>/src/test/java/com/bank/service/internal/FlatFeePolicyTest.java
text:
```scala
package com.bank.service.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.@@assertThat;

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

```


#### Short summary: 

empty definition using pc, found symbol in pc: org/hamcrest/MatcherAssert#assertThat().