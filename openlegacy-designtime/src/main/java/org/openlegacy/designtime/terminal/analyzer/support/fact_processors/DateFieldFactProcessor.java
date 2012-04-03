package org.openlegacy.designtime.terminal.analyzer.support.fact_processors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openlegacy.definitions.support.SimpleDateFieldTypeDefinition;
import org.openlegacy.designtime.terminal.analyzer.ScreenFact;
import org.openlegacy.designtime.terminal.analyzer.ScreenFactProcessor;
import org.openlegacy.designtime.terminal.model.ScreenEntityDesigntimeDefinition;
import org.openlegacy.terminal.definitions.ScreenFieldDefinition;
import org.openlegacy.terminal.definitions.SimpleScreenFieldDefinition;
import org.openlegacy.utils.StringUtil;

import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

public class DateFieldFactProcessor implements ScreenFactProcessor {

	private final static Log logger = LogFactory.getLog(DateFieldFactProcessor.class);

	public boolean accept(ScreenFact screenFact) {
		return screenFact instanceof DateFieldFact;
	}

	public void process(ScreenEntityDesigntimeDefinition screenEntityDefinition, ScreenFact screenFact) {
		DateFieldFact dateFieldFact = (DateFieldFact)screenFact;

		SimpleScreenFieldDefinition leftFieldDefinition = (SimpleScreenFieldDefinition)dateFieldFact.getLeftField();
		ScreenFieldDefinition middleFieldDefinition = dateFieldFact.getMiddleField();
		ScreenFieldDefinition rightFieldDefinition = dateFieldFact.getRightField();

		Map<String, ScreenFieldDefinition> fieldsDefinitions = screenEntityDefinition.getFieldsDefinitions();
		// verifying all fields belongs to the same screen entity. Couldn't do it in drools with memberOf. TODO
		if (leftFieldDefinition != null && !fieldsDefinitions.containsValue(leftFieldDefinition)) {
			return;
		}
		if (middleFieldDefinition != null && !fieldsDefinitions.containsValue(dateFieldFact.getMiddleField())) {
			return;
		}

		if (rightFieldDefinition != null && !fieldsDefinitions.containsValue(dateFieldFact.getRightField())) {
			return;
		}

		SimpleDateFieldTypeDefinition fieldTypeDefinition = new SimpleDateFieldTypeDefinition(
				dateFieldFact.getLeftField().getPosition().getColumn(), middleFieldDefinition.getPosition().getColumn(),
				rightFieldDefinition.getPosition().getColumn());
		leftFieldDefinition.setFieldTypeDefinition(fieldTypeDefinition);
		leftFieldDefinition.setJavaType(Date.class);

		screenEntityDefinition.getReferredClasses().add(Date.class.getName());

		// remove all 3 fields date fields and add with the correct name. The middle/last date fields may take the label field
		// name as drools as can't verify analysis order
		fieldsDefinitions.remove(leftFieldDefinition.getName());
		if (middleFieldDefinition != null) {
			fieldsDefinitions.remove(middleFieldDefinition.getName());
		}
		if (rightFieldDefinition != null) {
			fieldsDefinitions.remove(rightFieldDefinition.getName());
		}

		// set the length as all 3 - as place holder
		leftFieldDefinition.setLength(rightFieldDefinition.getEndPosition().getColumn()
				- leftFieldDefinition.getPosition().getColumn());

		// re-add the field
		String fieldName = StringUtil.toJavaFieldName(dateFieldFact.getLabelField().getValue());
		leftFieldDefinition.setName(fieldName);
		fieldsDefinitions.put(fieldName, leftFieldDefinition);

		logger.info(MessageFormat.format("Set field {0} to be date field", leftFieldDefinition.getName()));
	}
}