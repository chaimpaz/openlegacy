package org.openlegacy.terminal.support;

import org.openlegacy.FieldFormatter;
import org.openlegacy.terminal.FieldComparator;
import org.openlegacy.terminal.ScreenEntity;

import javax.inject.Inject;

public class ContentDifferentFieldComparator implements FieldComparator {

	@Inject
	private FieldFormatter fieldFormatter;

	public boolean isFieldModified(ScreenEntity screenEntity, String fieldName, Object oldValue, Object newValue) {
		if (newValue == null) {
			return false;
		}
		if (oldValue == null && newValue != null) {
			return true;
		}
		oldValue = fieldFormatter.format(oldValue.toString());
		return !oldValue.equals(newValue);
	}

}