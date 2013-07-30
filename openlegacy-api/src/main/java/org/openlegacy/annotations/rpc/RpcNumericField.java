package org.openlegacy.annotations.rpc;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcNumericField {

	double minimumValue() default 0.0;

	double maximumValue() default 0.0;

	int decimalPlaces() default 0;

}
