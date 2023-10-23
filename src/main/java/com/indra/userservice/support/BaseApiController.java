package com.indra.userservice.support;

import com.indra.userservice.constant.CommonConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import java.net.URI;
import java.util.Locale;

public class BaseApiController {
    @Autowired
    MessageSource messageSource;

    protected ResponseEntity makeMessage(HttpStatus httpStatus, URI createUri, String messageCode, Object[] params, String status, Locale locale) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(status);
        responseMessage.setMessage(messageSource.getMessage(messageCode, params, locale));

        if(httpStatus==HttpStatus.CREATED) {
            return ResponseEntity.status(httpStatus).location(createUri).body(responseMessage) ;
        }

        return ResponseEntity.status(httpStatus).body(responseMessage) ;
    }

    protected ResponseEntity makeMessageForSuccess(Locale locale) {
        return makeMessage(HttpStatus.OK, null,"success.message", null, CommonConstants.SUCCESS, locale);
    }

    protected ResponseEntity makeMessageForCreated(URI createUri, Locale locale) {
        return makeMessage(HttpStatus.CREATED, createUri, "success.message", null, CommonConstants.SUCCESS, locale);
    }

    protected  ResponseEntity makeDataMessage(HttpStatus httpStatus, URI createUri, String messageCode, Object[] params, String status, Object data, Locale locale) {
        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setStatus(status);
        responseMessage.setMessage(messageSource.getMessage(messageCode, params, locale));
        responseMessage.setData(data);

        if(httpStatus==HttpStatus.CREATED) {
            return ResponseEntity.status(httpStatus).location(createUri).body(responseMessage) ;
        }
        
        return ResponseEntity.status(httpStatus).body(responseMessage);
    }

    protected ResponseEntity makeDataMessageForSuccess(Object data, Locale locale) {
        return makeDataMessage(HttpStatus.OK, null, "success.message", null, CommonConstants.SUCCESS, data, locale);
    }

    protected ResponseEntity makeDataMessageForCreated(URI createUri, Object data, Locale locale) {
        return makeDataMessage(HttpStatus.CREATED,createUri, "success.message", null, CommonConstants.SUCCESS, data, locale);
    }


    protected ResponseEntity makeDataMessageForValidation(Object data, Locale locale) {
        return makeDataMessage(HttpStatus.BAD_REQUEST, null, "validation.message", null, CommonConstants.VALIDATION, data, locale);
    }

    public boolean doFormValidation(BindingResult bindResult, ValidationObject validationObject) {
        if (bindResult.hasErrors()) {
            validationObject.setValidationCode(bindResult.getFieldError().getCode());
            validationObject.setValidationField(bindResult.getFieldError().getField());
            validationObject.setValidationDescription(bindResult.getFieldError().getDefaultMessage());
            return true;
        }
        return false;
    }

}
