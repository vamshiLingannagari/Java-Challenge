package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.NegativeAmountException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
        "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }
  
  public int addBalance(String accountId, BigDecimal amount)
                              throws NegativeAmountException{
		Account account = null;
		try{
		    account = accounts.get(accountId);
		    if(account != null){
		    	if(amount.compareTo(BigDecimal.ZERO) == 1){
		    	    account.setBalance(account.getBalance().add(amount));	
		    	    accounts.put(accountId, account);
		    	}else{
		    		throw new NegativeAmountException("Negative Transfer amount not Acceptable");
		    	}
		    }else{
		    	throw new NullPointerException();
		    }
		}catch(NullPointerException e){
			e.printStackTrace();
		}
		return 1;
	}

	public int reduceBalance(String accountId, BigDecimal amount)
	                throws InsufficientBalanceException,NegativeAmountException{
		int result=1;
		Account account = null;
		try{
			account = accounts.get(accountId);
			if(account != null){
				if(amount.compareTo(BigDecimal.ZERO) == 1){
					if(account.getBalance().compareTo(amount) == 1){
						account.setBalance(account.getBalance().subtract(amount));
					}else{
						result=0;						
					}					
				}else{
					 result=0;					
				}				
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

}
