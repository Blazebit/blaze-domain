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

package com.blazebit.domain.runtime.model;

import java.util.Map;

/**
 * A domain function return type resolver.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainFunctionTypeResolver {

    /**
     * Resolves the domain function return type for the given argument type assignments.
     *
     * @param domainModel The domain model
     * @param function The domain function
     * @param argumentTypes The domain function argument types
     * @return the resolved function return type
     * @throws DomainTypeResolverException when the function is invoked with the wrong argument types
     */
    public DomainType resolveType(DomainModel domainModel, DomainFunction function, Map<DomainFunctionArgument, DomainType> argumentTypes);

}
