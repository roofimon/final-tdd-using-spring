package com.bank.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.bank.repository.AccountRepository;
import com.bank.service.TransferService;

public class AccountControllerAccessorsTest {

	@Test
	public void gettersReturnInjectedDependencies() {
		AccountController controller = new AccountController();
		AccountRepository repo = new com.bank.repository.internal.SimpleAccountRepository();
		controller.setRepository(repo);

		TransferService svc = new com.bank.service.internal.DefaultTransferService(repo,
				new com.bank.service.internal.ZeroFeePolicy());
		controller.setService(svc);

		assertNotNull(controller.getRepository());
		assertNotNull(controller.getService());
	}
}
