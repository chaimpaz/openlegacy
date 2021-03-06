// WARNING: DO NOT EDIT THIS FILE.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.openlegacy.terminal.samples.model;

import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.openlegacy.terminal.samples.model.Items.ItemsRecord;

privileged @SuppressWarnings("unused")
aspect ItemsRecordTable_Aspect {

	public Integer ItemsRecord.getAction_() {
		return this.action_;
	}

	public void ItemsRecord.setAction_(Integer action_) {
		this.action_ = action_;
	}

	public String ItemsRecord.getAlphaSearch() {
		return this.alphaSearch;
	}

	public void ItemsRecord.setAlphaSearch(String alphaSearch) {
		this.alphaSearch = alphaSearch;
	}

	public String ItemsRecord.getItemDescription() {
		return this.itemDescription;
	}

	public void ItemsRecord.setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}

	public Integer ItemsRecord.getItemNumber() {
		return this.itemNumber;
	}

	public void ItemsRecord.setItemNumber(Integer itemNumber) {
		this.itemNumber = itemNumber;
	}

	public int ItemsRecord.hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	public boolean ItemsRecord.equals(Object other) {
		// TODO exclude terminal fields
		return EqualsBuilder.reflectionEquals(this, other);
	}
}
