package com.stockmanagement.Exception;

import org.springframework.http.HttpStatus;

public class ProductServiceExceptionHandler extends RuntimeException {
private HttpStatus httpStatus;
	
	public ProductServiceExceptionHandler (String message, HttpStatus httpStatus) {
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
