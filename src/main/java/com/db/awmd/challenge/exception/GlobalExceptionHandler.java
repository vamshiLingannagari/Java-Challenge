package com.db.awmd.challenge.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
	
private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
		
	@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Illegal Argument passed")
	@ExceptionHandler(IllegalArgumentException.class)
	public void handleIllegalArgumentException(IllegalArgumentException ex){		
		logger.error("Illegal Argument passed", ex);			
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Not all mandatory fields are passed in request")
	@ExceptionHandler(EmptyFieldsException.class)
	public void handleEmptyFieldsException(EmptyFieldsException ex){
		logger.error("Not all Mandatory fields are passed in request", ex);
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Transfer of Negative amount not acceptable")
	@ExceptionHandler(NegativeAmountException.class)
	public void handleNegativeAmountException(NegativeAmountException ex){
		logger.error("Transfer of Negative amount not acceptable", ex);
	}
	
	@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Insufficient balance in the Account to Transfer")
	@ExceptionHandler(InsufficientBalanceException.class)
	public void handleInsufficientBalanceException(InsufficientBalanceException ex){
		logger.error("Insufficient balance in the Account to Transfer", ex);
	}
	

}
