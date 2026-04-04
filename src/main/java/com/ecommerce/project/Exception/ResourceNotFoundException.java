package com.ecommerce.project.Exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException {

    String resourceName;
    String field;
    String fieldName;
    Long fieldId;


    /**
     * Creates exception when resource lookup fails by string field value.
     */
    public ResourceNotFoundException(String resourceName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s", resourceName, field, fieldName));
        this.field=field;
        this.resourceName=resourceName;
        this.fieldName=fieldName;
    }

    /**
     * Creates exception when resource lookup fails by numeric field value.
     */
    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourceName, field, fieldId));
        this.field=field;
        this.resourceName=resourceName;
        this.fieldId=fieldId;
    }



}
