package org.openlegacy.designtime.rpc.source.parsers;

public class CobolFieldFormaterFactory implements FieldFormaterFactory {

	private static final char SIGHN_SYMBOL = 'S';
	private static final char DIGIT_SYMBOL = '9';
	private static final char SCAL_SYMBOL = 'P';

	private enum CobolFieldTypes {
		SIMPLE,
		NUMERIC,
		HIRARCHY;
	}

	private static boolean isNumber(char firstChar) {

		if (firstChar == SIGHN_SYMBOL || firstChar == DIGIT_SYMBOL || firstChar == SCAL_SYMBOL) {
			return true;
		}
		return false;
	}

	private static CobolFieldTypes getCobolFieldTypes(String flatePicture) {
		if (flatePicture == null) {
			return CobolFieldTypes.HIRARCHY;
		}

		if (isNumber(flatePicture.charAt(0))) {
			return CobolFieldTypes.NUMERIC;
		}

		return CobolFieldTypes.SIMPLE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.FieldFormaterFactory#getObject(java.lang.String)
	 */
	public FieldFormater getObject(String flatePicture) {
		CobolFieldTypes cobolFieldType = getCobolFieldTypes(flatePicture);
		if (CobolFieldTypes.NUMERIC == cobolFieldType) {
			return new CobolNuberFormater(flatePicture);
		}
		if (CobolFieldTypes.SIMPLE == cobolFieldType) {
			return new CobolTextFormater(flatePicture);
		}
		return null;

	}
}
