package org.openlegacy.designtime.rpc.source.parsers;

import org.openlegacy.definitions.FieldTypeDefinition;
import org.openlegacy.definitions.support.SimpleNumericFieldTypeDefinition;

public class CobolNuberFormater implements FieldFormater {

	private static final char DIGIT_SYMBOL = '9';
	private static final char DOT_SYMBOL = 'V';
	private static final char SIGHN_SYMBOL = 'S';
	private static final char EXP_SYMBOL = 'E';
	private static final char SCAL_SYMBOL = 'P';

	private boolean signed = false;
	private int scale = 0;
	private int digitBeforeDot = 0;
	private int digitAfterDot = 0;
	private int exponentDigits = 0;

	CobolNuberFormater(String flatPic) {
		int idx = 0;
		int lastCharIdx = flatPic.length() - 1;
		if (flatPic.charAt(0) == SIGHN_SYMBOL) {
			signed = true;
			idx++;
		}
		for (; flatPic.charAt(idx) == SCAL_SYMBOL; idx++) {
			scale++;
		}

		for (; idx <= lastCharIdx && flatPic.charAt(idx) == DIGIT_SYMBOL; idx++) {
			digitBeforeDot++;
		}

		if (idx < lastCharIdx && flatPic.charAt(idx) == DOT_SYMBOL) {
			idx++;
			for (; idx <= lastCharIdx && flatPic.charAt(idx) == DIGIT_SYMBOL; idx++) {
				digitAfterDot++;
			}
		}

		if (idx < lastCharIdx && flatPic.charAt(idx) == EXP_SYMBOL) {
			idx++;
			for (; idx <= lastCharIdx && flatPic.charAt(idx) == DIGIT_SYMBOL; idx++) {
				exponentDigits++;
			}
		}
		if (idx < lastCharIdx) {
			for (; idx <= lastCharIdx && flatPic.charAt(idx) == SCAL_SYMBOL; idx++) {
				scale--;
			}
		}

	}

	public boolean isSigned() {
		return signed;
	}

	public int getScale() {
		return scale;
	}

	public int getDigitBeforeDot() {
		return digitBeforeDot;
	}

	public int getDigitAfterDot() {
		return digitAfterDot;
	}

	public int getExponentDigits() {
		return exponentDigits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.CobolFormatDefinition#getLength()
	 */
	public double getLength() {

		return (digitBeforeDot + ((double)digitAfterDot) / 10);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.openlegacy.designtime.rpc.source.parsers.CobolFormatDefinition#getJavaType()
	 */
	public Class<?> getJavaType() {
		if (digitAfterDot > 0) {
			return Double.class;
		}
		return Integer.class;
	}

	public FieldTypeDefinition getType() {
		int maxVal = (int)(Math.pow(10, scale + digitBeforeDot) - 1);
		return new SimpleNumericFieldTypeDefinition(-maxVal, maxVal);
	}
}
