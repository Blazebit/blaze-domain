/*
 * Copyright 2019 - 2021 Blazebit.
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

package com.blazebit.domain.declarative.spi;

import java.lang.reflect.Type;

/**
 * A type resolver for generic types of Java type members.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface TypeResolver {

    /**
     * A type resolver that always returns null i.e. doesn't resolve a type.
     */
    public static final TypeResolver NOOP = new TypeResolver() {
        @Override
        public Object resolve(Class<?> contextClass, Type type) {
            return null;
        }
    };

    /**
     * Resolves the given type to a type name string, java type class or parameterized type.
     *
     *
     * @param contextClass The context class against which to resolve the type
     * @param type The type to resolve
     * @return the resolved type
     */
    public Object resolve(Class<?> contextClass, Type type);

}
