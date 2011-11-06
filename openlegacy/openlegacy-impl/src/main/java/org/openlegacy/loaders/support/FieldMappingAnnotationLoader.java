package org.openlegacy.loaders.support;

import org.openlegacy.HostEntitiesRegistry;
import org.openlegacy.annotations.screen.FieldMapping;
import org.openlegacy.loaders.FieldAnnotationsLoader;
import org.openlegacy.terminal.definitions.SimpleFieldMappingDefinition;
import org.openlegacy.terminal.support.SimpleScreenPosition;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;

@Component
public class FieldMappingAnnotationLoader implements FieldAnnotationsLoader {

	public boolean match(Annotation annotation) {
		return annotation.annotationType() == FieldMapping.class;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void load(HostEntitiesRegistry screenEntitiesRegistry, String fieldName, Annotation annotation,
			Class<?> containingClass) {
		FieldMapping fieldAnnotation = (FieldMapping)annotation;

		SimpleScreenPosition screenPosition = SimpleScreenPosition.newInstance(fieldAnnotation.row(), fieldAnnotation.column());
		SimpleFieldMappingDefinition fieldMappingDefinition = new SimpleFieldMappingDefinition(fieldName,
				fieldAnnotation.fieldType());
		fieldMappingDefinition.setScreenPosition(screenPosition);
		fieldMappingDefinition.setLength(fieldAnnotation.length());
		fieldMappingDefinition.setEditable(fieldAnnotation.editable());

		screenEntitiesRegistry.get(containingClass).getFieldsDefinitions().put(fieldName, fieldMappingDefinition);
	}

}
