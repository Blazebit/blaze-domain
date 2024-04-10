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

package com.blazebit.domain.runtime.model;

import java.util.List;

/**
 * A domain predicate type resolver.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainPredicateTypeResolver {

    /**
     * Resolves the domain type for applying a predicate on the given operand domain type.
     *
     * @param domainModel The domain model
     * @param domainTypes The operand domain types
     * @return the resolved type
     * @throws DomainTypeResolverException when one of the operands has an unsupported type
     */
    public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes);

}
