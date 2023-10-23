package com.indra.userservice.support;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidationObject {
    private String validationCode;
    private String validationField;

    private String validationDescription;

    public ValidationObject(String validationDescription) {
        this.validationDescription = validationDescription;
    }

    public ValidationObject() {}

}
