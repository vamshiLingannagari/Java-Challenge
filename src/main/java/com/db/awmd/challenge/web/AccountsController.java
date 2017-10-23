package com.db.awmd.challenge.web;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.exception.EmptyFieldsException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.exception.NegativeAmountException;
import com.db.awmd.challenge.service.AccountsService;

import java.math.BigDecimal;
import java.util.Map;

import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {
	
	Logger log = LoggerFactory.getLogger(AccountsController.class);

  private final AccountsService accountsService;

  @Autowired
  public AccountsController(AccountsService accountsService) {
    this.accountsService = accountsService;
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
    log.info("Creating account {}", account);

    try {
    this.accountsService.createAccount(account);
    } catch (DuplicateAccountIdException daie) {
      return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
    }

    return new ResponseEntity<>(HttpStatus.CREATED);
  }

  @GetMapping(path = "/{accountId}")
  public Account getAccount(@PathVariable String accountId) {
    log.info("Retrieving account for id {}", accountId);
    return this.accountsService.getAccount(accountId);
  }  
  
  @PostMapping(value="/transfer",consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getTransferDetails(@RequestBody Map<String, String> payload){
		String result = null;
		try{
			if(payload.get("accountFrom") == null){
				throw new EmptyFieldsException("Sender Account Id Missing");
			}else if(payload.get("accountTo") == null){
				throw new EmptyFieldsException("Receivers Account Id Missing");
			}else if(payload.get("amount") == null){
				throw new EmptyFieldsException("Transfer Amount Missing");
			}else{
			  if(accountsService != null){
				if(new BigDecimal(payload.get("amount")).compareTo(BigDecimal.ZERO) == 1){
				     result = accountsService.fundsTransfer(payload.get("accountFrom"), payload.get("accountTo"), new BigDecimal(payload.get("amount")));
				     if(result.equals(AccountsService.STATUS_FAILURE)){
				    	 throw new InsufficientBalanceException("Insufficient Funds in the Account to Transfer");
				     }
				}else{
					throw new NegativeAmountException("Negative Transfer amount not Acceptable");
				}
			  }
			}
		}catch(NegativeAmountException nae){
			return new ResponseEntity<>(nae.getMessage(), HttpStatus.BAD_REQUEST);
		}catch(InsufficientBalanceException ibe){
			return new ResponseEntity<>(ibe.getMessage(), HttpStatus.BAD_REQUEST);
		}catch(EmptyFieldsException efe){
			return new ResponseEntity<>(efe.getMessage(), HttpStatus.BAD_REQUEST);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return new ResponseEntity<String>(result, HttpStatus.OK);
	} 

}
