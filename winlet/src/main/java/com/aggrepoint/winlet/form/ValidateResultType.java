package com.aggrepoint.winlet.form;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public enum ValidateResultType {
	FAILED_CONTINUE, // Validate failed. Continue to do next validation. 
	FAILED_SKIP_PROPERTY, // Validation failed. Skip following validations within same property. 
	FAILED_SKIP_ALL, // Validate failed. Skip all following validations on this property and remain properties. 
	PASS_CONTINUE, // Validate successful. Continue to do next validation.
	PASS_SKIP_PROPERTY, //Validate successful. Skip following validations within same property.
	PASS_SKIP_ALL //Validate successful. Skip all following validations on this property and remain properties.
}
