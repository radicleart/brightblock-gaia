package org.brightblock.gaia.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<Exception> handleResourceAccessException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		logger.error("bitcoin: error = " + e.getMessage(), e);
		return new ResponseEntity<Exception>(e, HttpStatus.SERVICE_UNAVAILABLE);
	}

	@ExceptionHandler(IllegalAccessException.class)
	public ResponseEntity<Exception> handleIllegalAccessException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		logger.error("reporting-error: [request = " + request.toString() + "] error = " + e.getMessage(), e);
		return new ResponseEntity<Exception>(e, HttpStatus.FORBIDDEN);
	}

	@ExceptionHandler
	public ResponseEntity<Exception> handleException(HttpServletRequest request, HttpServletResponse response, Exception e) {
		logger.error("reporting-error: [request = " + request.toString() + "] error = " + e.getMessage(), e);
		return new ResponseEntity<Exception>(e, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
