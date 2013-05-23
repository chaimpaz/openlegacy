package org.openlegacy.annotations.rpc;

import org.openlegacy.FieldType;
import org.openlegacy.annotations.screen.AnnotationConstants;
import org.openlegacy.rpc.RpcFieldTypes;

public @interface RpcField {

	Direction direction();

	int length();

	Class<? extends FieldType> fieldType() default RpcFieldTypes.General.class;

	String displayName() default AnnotationConstants.NULL;

	String sampleValue() default "";

	String helpText() default "";
}
