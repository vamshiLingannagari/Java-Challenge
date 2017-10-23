package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {
	
	public static final String STATUS_SUCCESS = "Success";
	public static final String STATUS_FAILURE = "Failure";
	
	private Lock lock =new ReentrantLock();

  @Getter
  private final AccountsRepository accountsRepository ;
  
  @Autowired
  @Qualifier("EmailNotification")
  private NotificationService notificationService;

  public AccountsRepository getAccountsRepository() {
	return accountsRepository;
 }
  
  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }
  
  public String fundsTransfer(String fromAccount, String toAccount, BigDecimal amount){
		int result;
		String transferDescription ="Amount of "+ amount;
		try{
			lock.lock();
			if(accountsRepository != null){
				result = this.accountsRepository.reduceBalance(fromAccount, amount);
				if(result == 1){
					this.accountsRepository.addBalance(toAccount, amount);
					try{
					    this.notificationService.notifyAboutTransfer(accountsRepository.getAccount(fromAccount), transferDescription);
					    this.notificationService.notifyAboutTransfer(accountsRepository.getAccount(toAccount), transferDescription);
					}catch(Exception e){
						
					}
				}else{
					return AccountsService.STATUS_FAILURE;
				}
				
			}else{
				throw new NullPointerException();
			}
			
		}catch(NullPointerException e){
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		
		return AccountsService.STATUS_SUCCESS;
	}
    
}
