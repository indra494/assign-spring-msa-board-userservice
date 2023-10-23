package com.indra.userservice.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseMessage {

    private String status = "SUCCESS";
    private String message = "";
    private String errorMessage = "";
    private String errorCode = "";
    private Object data = "";

    public ResponseMessage(String message) {
        this.message = message;
    }
}
