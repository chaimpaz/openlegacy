/*******************************************************************************
 * Copyright (c) 2012 OpenLegacy Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     OpenLegacy Inc. - initial API and implementation
 *******************************************************************************/
package org.openlegacy.annotations.rpc;

import org.openlegacy.EntityType;
import org.openlegacy.annotations.screen.AnnotationConstants;
import org.openlegacy.rpc.RpcEntityType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RpcEntity {

	String name() default AnnotationConstants.NULL;

	String displayName() default AnnotationConstants.NULL;

	Languages language() default Languages.UNDEFINED;

	Class<? extends EntityType> rpcType() default RpcEntityType.General.class;
}
