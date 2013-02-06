package com.bank.service.internal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;

import com.bank.domain.Account;
import com.bank.domain.InsufficientFundsException;
import com.bank.domain.TransferReceipt;

import com.bank.repository.AccountNotFoundException;
import com.bank.repository.AccountRepository;
import com.bank.repository.internal.SimpleAccountRepository;
import static com.bank.repository.internal.SimpleAccountRepository.Data.A123_ID;
import static com.bank.repository.internal.SimpleAccountRepository.Data.A123_INITIAL_BAL;
import static com.bank.repository.internal.SimpleAccountRepository.Data.C456_ID;
import static com.bank.repository.internal.SimpleAccountRepository.Data.C456_INITIAL_BAL;
import static com.bank.repository.internal.SimpleAccountRepository.Data.Z999_ID;

import com.bank.service.FeePolicy;
import com.bank.service.OutOfServiceException;
import com.bank.service.TimeService;
import com.bank.service.TransferService;

public class DefaultTransferServiceTest {

    private AccountRepository accountRepository;
    private TransferService transferService;

    @Before
    public void setUp() {
        accountRepository = new SimpleAccountRepository();
        FeePolicy feePolicy = new ZeroFeePolicy();
        
        transferService = new DefaultTransferService(accountRepository, feePolicy);

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL));
    }

    @Test
    public void testTransfer() throws InsufficientFundsException {
        double transferAmount = 100.00;

        //when
        TransferReceipt receipt = transferService.transfer(transferAmount, A123_ID, C456_ID);

        //then
        assertThat(receipt.getTransferAmount(), equalTo(transferAmount));
        assertThat(receipt.getFinalSourceAccount().getBalance(), equalTo(A123_INITIAL_BAL - transferAmount));
        assertThat(receipt.getFinalDestinationAccount().getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL - transferAmount));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));
    }

    @Test
    public void testTransferUsingDynamicStub() throws InsufficientFundsException {
    	//given
        double transferAmount = 100.00;
        String srcAccId = "A123";
        Account srcAcc = new Account(srcAccId, 100.00);
        String desAccId = "C456";
        Account desAcc = new Account(desAccId, 0.00);

        AccountRepository mockAccReop = mock(AccountRepository.class);
        when(mockAccReop.findById(srcAccId)).thenReturn(srcAcc);
        when(mockAccReop.findById(desAccId)).thenReturn(desAcc);
        
        FeePolicy mockFeePolicy = mock(FeePolicy.class);
        when(mockFeePolicy.calculateFee(anyDouble())).thenReturn(0.00);
        
        TransferService transferService = new DefaultTransferService(mockAccReop, mockFeePolicy);
        

        //when
        TransferReceipt receipt = transferService.transfer(transferAmount, srcAccId, desAccId);

        //then
        assertThat(receipt.getTransferAmount(), equalTo(transferAmount));
        assertThat(receipt.getFinalSourceAccount().getBalance(), equalTo(0.00));
        assertThat(receipt.getFinalDestinationAccount().getBalance(), equalTo(100.00));
    }

    
    @Test
    public void testTransferWithCheckingTimeService() throws InsufficientFundsException {
    	//given
        double transferAmount = 100.00;
        TimeService mockTimeService = mock(TimeService.class);
        when(mockTimeService.isServiceAvailable(any(LocalTime.class))).thenReturn(true);
        transferService.setTimeService(mockTimeService);

        //when
        TransferReceipt receipt = transferService.transfer(transferAmount, A123_ID, C456_ID);

        //then
        assertThat(receipt.getTransferAmount(), equalTo(transferAmount));
        assertThat(receipt.getFinalSourceAccount().getBalance(), equalTo(A123_INITIAL_BAL - transferAmount));
        assertThat(receipt.getFinalDestinationAccount().getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL - transferAmount));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));
        //verify behavior
        verify(mockTimeService).isServiceAvailable(any(LocalTime.class));
    }

    @Test
    public void testTransferWithCheckingOutofTimeService() throws InsufficientFundsException {
    	//given
        double transferAmount = 100.00;
        TimeService mockTimeService = mock(TimeService.class);
        when(mockTimeService.isServiceAvailable(any(LocalTime.class))).thenReturn(false);
        transferService.setTimeService(mockTimeService);

        //when
        try {
        	TransferReceipt receipt = transferService.transfer(transferAmount, A123_ID, C456_ID);
        	fail();
        } catch (OutOfServiceException e) {
        	//then
        	//verify behavior
            verify(mockTimeService).isServiceAvailable(any(LocalTime.class));
		}

    }
    
    @Test(expected=InsufficientFundsException.class)    
    public void testInsufficientFunds() throws InsufficientFundsException {
        double overage = 9.00;
        double transferAmount = A123_INITIAL_BAL + overage;

        //try {
            transferService.transfer(transferAmount, A123_ID, C456_ID);
            fail("expected InsufficientFundsException");
        //} catch (InsufficientFundsException ex) {
        //    assertThat(ex.getTargetAccountId(), equalTo(A123_ID));
        //    assertThat(ex.getOverage(), equalTo(overage));
        //}

        //assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL));
        //assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL));
    }

    @Test
    public void testNonExistentSourceAccount() throws InsufficientFundsException {
        try {
            transferService.transfer(1.00, Z999_ID, C456_ID);
            fail("expected AccountNotFoundException");
        } catch (AccountNotFoundException ex) {
        }

        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL));
    }

    @Test
    public void testNonExistentDestinationAccount() throws InsufficientFundsException {
        try {
            transferService.transfer(1.00, A123_ID, Z999_ID);
            fail("expected AccountNotFoundException");
        } catch (AccountNotFoundException ex) {
        }

        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL));
    }

    @Test
    public void testZeroTransferAmount() throws InsufficientFundsException {
        try {
            transferService.transfer(0.00, A123_ID, C456_ID);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testNegativeTransferAmount() throws InsufficientFundsException {
        try {
            transferService.transfer(-100.00, A123_ID, C456_ID);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testTransferAmountLessThanOneCent() throws InsufficientFundsException {
        try {
            transferService.transfer(0.009, A123_ID, C456_ID);
            fail("expected IllegalArgumentException");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testCustomizedMinimumTransferAmount() throws InsufficientFundsException {
        transferService.transfer(1.00, A123_ID, C456_ID); // should be fine
        transferService.setMinimumTransferAmount(10.00);
        transferService.transfer(10.00, A123_ID, C456_ID); // fine against new minimum
        try {
            transferService.transfer(9.00, A123_ID, C456_ID); // violates new minimum!
            fail("expected IllegalArgumentException on 9.00 transfer that violates 10.00 minimum");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testNonZeroFeePolicy() throws InsufficientFundsException {
        double flatFee = 5.00;
        double transferAmount = 95.00;
        transferService = new DefaultTransferService(accountRepository, new FlatFeePolicy(flatFee));
        transferService.transfer(transferAmount, A123_ID, C456_ID);
        assertThat(accountRepository.findById(A123_ID).getBalance(), equalTo(A123_INITIAL_BAL - transferAmount - flatFee));
        assertThat(accountRepository.findById(C456_ID).getBalance(), equalTo(C456_INITIAL_BAL + transferAmount));
    }

    @Test
    public void testMaximumTransferWithFlatFeePolicy() {
        double flatFee = 5.00;
        double transferAmout = 99.00;
        transferService = new DefaultTransferService(accountRepository, new FlatFeePolicy(flatFee));
        try {
            transferService.transfer(transferAmout, A123_ID, C456_ID);
            fail("expected InsufficientFundsException");
        } catch (InsufficientFundsException ex) {
        }
    }
}
