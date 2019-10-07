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

package com.blazebit.domain.boot.model;

import java.util.List;

/**
 * A function in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainFunctionDefinition extends MetadataDefinitionHolder<DomainFunctionDefinition> {

    /**
     * The name of the domain function.
     *
     * @return the name
     */
    public String getName();

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
     * The argument definitions for this domain function.
     *
     * @return the argument definitions
     */
    public List<DomainFunctionArgumentDefinition> getArgumentDefinitions();

}
