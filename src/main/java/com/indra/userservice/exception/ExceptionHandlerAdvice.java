package com.indra.userservice.exception;

import com.indra.userservice.constant.CommonConstants;
import com.indra.userservice.support.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Locale;

@Slf4j
@RestControllerAdvice()
public class ExceptionHandlerAdvice {
    @Autowired
    MessageSource messageSource;

    @ExceptionHandler(NoHandlerFoundException.class)
    public Object handle404(Exception e){
        Locale locale = LocaleContextHolder.getLocale();
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setErrorMessage(messageSource.getMessage("failure.message", null, locale));

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(responseMessage);
    }

    @ExceptionHandler(DataAccessException.class)
    public Object handleDataAccessException(DataAccessException e) {
        Locale locale = LocaleContextHolder.getLocale();
        log.error("#### database error :: ",e);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(CommonConstants.FAILURE);

        responseMessage.setErrorMessage(messageSource.getMessage("failure.message", null, locale));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);

    }

    // 500에러
    @ExceptionHandler(Exception.class)
    public Object handle(Exception e) {
        Locale locale = LocaleContextHolder.getLocale();
        log.error("#### system error :: ",e);

        ResponseMessage responseMessage = new ResponseMessage();
        if(e instanceof AuthException)
            responseMessage.setStatus(CommonConstants.AUTH_FAILURE);
        else
            responseMessage.setStatus(CommonConstants.FAILURE);

        if(e instanceof BasicException) {
            responseMessage.setErrorMessage(((BasicException)e).getMessage());
        } else {
            responseMessage.setErrorMessage(messageSource.getMessage("failure.message", null, locale));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMessage);

    }

}
