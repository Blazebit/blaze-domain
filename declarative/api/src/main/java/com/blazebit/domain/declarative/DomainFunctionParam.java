/*
 * Copyright 2019 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.domain.declarative;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a domain function parameter.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface DomainFunctionParam {

    /**
     * The domain function argument name.
     *
     * @return the function argument name
     */
    String value() default "";

    /**
     * The domain function argument java type.
     *
     * @return the function argument java type
     */
    Class<?> type() default void.class;

    /**
     * The domain function argument type name.
     *
     * @return the function argument type name
     */
    String typeName() default "";

    /**
     * Whether the domain function argument type is a collection domain type.
     *
     * @return whether the domain function argument type is a collection domain type
     */
    boolean collection() default false;

}
