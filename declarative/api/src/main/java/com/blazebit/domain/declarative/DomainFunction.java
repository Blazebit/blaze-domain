/*
 * Copyright 2019 - 2022 Blazebit.
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

import com.blazebit.domain.runtime.model.DomainFunctionTypeResolver;
import com.blazebit.domain.runtime.model.DomainFunctionVolatility;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as being a domain function.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface DomainFunction {

    /**
     * The name of the domain function.
     *
     * @return the name of the domain function
     */
    String value() default "";

    /**
     * The return java type of the domain function.
     *
     * @return the return java type of the domain function
     */
    Class<?> type() default void.class;

    /**
     * The return type name of the domain function.
     *
     * @return the return type name of the domain function
     */
    String typeName() default "";

    /**
     * Whether the return type is a collection domain type.
     *
     * @return whether the return type is a collection domain type
     */
    boolean collection() default false;

    /**
     * The type resolver for the domain function type.
     *
     * @return the type resolver for the domain function type
     * @see com.blazebit.domain.runtime.model.StaticDomainFunctionTypeResolvers
     */
    Class<? extends DomainFunctionTypeResolver> typeResolver() default DomainFunctionTypeResolver.class;

    /**
     * The minimum number of arguments that are necessary for this function.
     *
     * @return The minimum number of arguments that are necessary for this function
     */
    int minArguments() default -1;

    /**
     * The domain function volatility.
     *
     * @return the domain function volatility
     */
    DomainFunctionVolatility volatility() default DomainFunctionVolatility.IMMUTABLE;

}
