package org.openlegacy.definitions;

/**
 * Defines a extended numeric field type registry information stored within {@link NumericFieldDefinition}.
 */

public interface ExtendedNumericFieldTypeDefinition extends NumericFieldTypeDefinition {

	int getDecimalPlaces();
}
