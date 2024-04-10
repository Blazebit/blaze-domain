/*
 * Copyright 2019 - 2024 Blazebit.
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

package com.blazebit.domain.boot.model;

/**
 * A function argument of a domain function definition.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainFunctionArgumentDefinition extends MetadataDefinitionHolder {

    /**
     * The name of the function argument or <code>null</code>.
     *
     * @return the name of the argument or <code>null</code>
     */
    public String getName();

    /**
     * The 0-based positional index of the function argument.
     *
     * @return the 0-based positional index
     */
    public int getIndex();

    /**
     * The type name of the function argument or <code>null</code>.
     *
     * @return the type name of the function argument or <code>null</code>
     */
    public String getTypeName();

    /**
     * Whether the type is a collection.
     *
     * @return <code>true</code> if the type is a collection, <code>false</code> otherwise
     * @since 2.0.3
     */
    public boolean isCollection();

}
