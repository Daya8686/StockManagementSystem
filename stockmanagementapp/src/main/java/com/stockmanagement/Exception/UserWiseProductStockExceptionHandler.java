package com.stockmanagement.Exception;

import org.springframework.http.HttpStatus;

public class UserWiseProductStockExceptionHandler extends RuntimeException {
private HttpStatus httpStatus;
	
	public UserWiseProductStockExceptionHandler (String message, HttpStatus httpStatus) {
		super(message);
		this.httpStatus=httpStatus;
		
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	} 

}
