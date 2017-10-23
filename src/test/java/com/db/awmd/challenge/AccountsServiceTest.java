package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;
  
  @Mock
  private NotificationService notificationService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }
  
  @Test
  public void fundsTransferTest() throws Exception{
	  
	  Account fromAccount = new Account("Id-1234");
	  fromAccount.setBalance(new BigDecimal(1000));
	  this.accountsService.createAccount(fromAccount);
	  Account toAccount = new Account("Id-4567");
	  toAccount.setBalance(new BigDecimal(500));
	  this.accountsService.createAccount(toAccount);
	  
	  int amount = 200;
	  
	  assertThat(this.accountsService.fundsTransfer(fromAccount.getAccountId(), toAccount.getAccountId(), new BigDecimal(amount)).equals(AccountsService.STATUS_SUCCESS));
	  assertThat(this.accountsService.getAccount("Id-1234").getBalance()).isEqualTo(new BigDecimal(800));
	  assertThat(this.accountsService.getAccount("Id-4567").getBalance()).isEqualTo(new BigDecimal(700));
  }
  
  @Test
  public void fundsTransferWithInsufficientBalance() throws Exception{
	  Account fromAccount = new Account("Id-12345");
	  fromAccount.setBalance(new BigDecimal(1000));
	  this.accountsService.createAccount(fromAccount);
	  Account toAccount = new Account("Id-45678");
	  toAccount.setBalance(new BigDecimal(500));
	  this.accountsService.createAccount(toAccount);
	  
	  int amount = 1200;
	  
	  assertThat(this.accountsService.fundsTransfer(fromAccount.getAccountId(), toAccount.getAccountId(), new BigDecimal(amount)).equals(AccountsService.STATUS_FAILURE));
	  assertThat(this.accountsService.getAccount("Id-12345").getBalance()).isEqualTo(new BigDecimal(1000));
	  assertThat(this.accountsService.getAccount("Id-45678").getBalance()).isEqualTo(new BigDecimal(500)); 
  }
  
  @Test
  public void fundsTransferWithNegativeAmount() throws Exception{
	  Account fromAccount = new Account("Id-12354");
	  fromAccount.setBalance(new BigDecimal(1000));
	  this.accountsService.createAccount(fromAccount);
	  Account toAccount = new Account("Id-45687");
	  toAccount.setBalance(new BigDecimal(500));
	  this.accountsService.createAccount(toAccount);	  
	  int amount = -100;
	  
	  assertThat(this.accountsService.fundsTransfer(fromAccount.getAccountId(), toAccount.getAccountId(), new BigDecimal(amount)).equals(AccountsService.STATUS_FAILURE));
	  assertThat(this.accountsService.getAccount("Id-12354").getBalance()).isEqualTo(new BigDecimal(1000));
	  assertThat(this.accountsService.getAccount("Id-45687").getBalance()).isEqualTo(new BigDecimal(500));
	  		  
  }
  
}
