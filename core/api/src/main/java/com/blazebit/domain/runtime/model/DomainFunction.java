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

package com.blazebit.domain.runtime.model;

import java.util.List;

/**
 * A function in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainFunction extends MetadataHolder {

    /**
     * The name of the domain function.
     *
     * @return the name of the domain function
     */
    public String getName();

    /**
     * The volatility of the domain function.
     *
     * @return the volatility of the domain function.
     */
    public DomainFunctionVolatility getVolatility();

    /**
     * The minimum argument count for the function.
     *
     * @return the minimum function argument count
     */
    public int getMinArgumentCount();

    /**
     * The maximum argument count for the function.
     *
     * @return the maximum function argument count
     */
    public int getArgumentCount();

    /**
     * The domain function arguments.
     *
     * @return the domain function arguments
     */
    public List<? extends DomainFunctionArgument> getArguments();

    /**
     * The domain function argument with the given name.
     *
     * @param argumentName The name of the desired argument
     * @return the domain function argument or <code>null</code>
     */
    public DomainFunctionArgument getArgument(String argumentName);

    /**
     * The domain function argument at the given index.
     *
     * @param argumentIndex The index of the desired argument
     * @return the domain function argument
     * @throws IndexOutOfBoundsException if the <code>argumentIndex &gt;= argumentCount</code>
     */
    public DomainFunctionArgument getArgument(int argumentIndex);

    /**
     * The domain function result type if fixed, otherwise <code>null</code>.
     *
     * @return the fixed result type or <code>null</code>
     */
    public DomainType getResultType();
}
