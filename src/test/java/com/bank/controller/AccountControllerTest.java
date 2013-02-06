package com.bank.controller;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import org.junit.Test;

import com.bank.domain.Account;
import com.bank.domain.InsufficientFundsException;
import com.bank.repository.AccountRepository;
import com.bank.service.TransferService;


public class AccountControllerTest {
	@Test
    public void testHandleById() {
    	//given
    	String accId = "A123";
    	AccountController controller = new AccountController();
    	Account account = new Account(accId, 100d);
    	
    	AccountRepository repository = mock(AccountRepository.class);
    	when(repository.findById(anyString())).thenReturn(account);
    	controller.setRepository(repository);
    	
    	//when
    	Account result = controller.handleById(accId);
    	
    	//then
    	assertEquals(account, result);
	}
	
	@Test
	public void testHandleTransfer() throws InsufficientFundsException {
    	//given
		String srcId = "A123";
		String destId = "B123";
		AccountController controller = new AccountController();
		
		TransferService service = mock(TransferService.class);
		controller.setService(service);
		
    	//when
		controller.handleTransfer(srcId, 100d, destId);
		
    	//then
		verify(service).transfer(100d, srcId, destId);
	}
}
