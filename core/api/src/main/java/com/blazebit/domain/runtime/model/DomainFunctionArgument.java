/*
 * Copyright 2019 - 2020 Blazebit.
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

package com.blazebit.domain.runtime.model;

/**
 * Represents the argument to a domain function.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainFunctionArgument extends MetadataHolder {

    /**
     * The owner domain function.
     *
     * @return the owner domain function
     */
    public DomainFunction getOwner();

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
    public int getPosition();

    /**
     * The domain type of the function argument or <code>null</code>.
     *
     * @return the domain type of the function argument or <code>null</code>
     */
    public DomainType getType();

}
