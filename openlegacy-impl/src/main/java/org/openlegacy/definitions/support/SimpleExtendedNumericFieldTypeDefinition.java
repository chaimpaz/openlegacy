package org.openlegacy.definitions.support;

import org.openlegacy.definitions.ExtendedNumericFieldTypeDefinition;

public class SimpleExtendedNumericFieldTypeDefinition extends SimpleNumericFieldTypeDefinition implements ExtendedNumericFieldTypeDefinition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int decimalPlaces = 0;

	public SimpleExtendedNumericFieldTypeDefinition() {
		super();
	}

	public SimpleExtendedNumericFieldTypeDefinition(double minimumValue, double maximumValue, int decimalPlaces) {
		super(minimumValue, maximumValue);
		this.decimalPlaces = decimalPlaces;
	}

	public int getDecimalPlaces() {
		return decimalPlaces;
	}

}
