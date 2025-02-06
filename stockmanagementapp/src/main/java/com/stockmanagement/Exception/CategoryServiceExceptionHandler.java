package com.stockmanagement.Exception;

import org.springframework.http.HttpStatus;

public class CategoryServiceExceptionHandler extends RuntimeException {
private HttpStatus httpStatus;
	
	public CategoryServiceExceptionHandler (String message, HttpStatus httpStatus) {
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
