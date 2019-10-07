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

package com.blazebit.domain.runtime.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A domain predicate type resolver utility that caches static resolvers.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class StaticDomainPredicateTypeResolvers {

    private static final Map<String, DomainPredicateTypeResolver> RETURNING_TYPE_NAME_CACHE = new HashMap<>();
    private static final Map<Class<?>, DomainPredicateTypeResolver> RETURNING_JAVA_TYPE_CACHE = new HashMap<>();

    private StaticDomainPredicateTypeResolvers() {
    }

    /**
     * Returns a domain predicate type resolver that always returns the domain type with the given type name.
     *
     * @param typeName The static domain type name
     * @return the domain predicate type resolver
     */
    public static DomainPredicateTypeResolver returning(final String typeName) {
        DomainPredicateTypeResolver domainOperationTypeResolver = RETURNING_TYPE_NAME_CACHE.get(typeName);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new DomainPredicateTypeResolver() {
                @Override
                public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
                    return domainModel.getType(typeName);
                }
            };
            RETURNING_TYPE_NAME_CACHE.put(typeName, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * Returns a domain predicate type resolver that always returns the domain type with the given java type.
     *
     * @param javaType The static domain java type
     * @return the domain predicate type resolver
     */
    public static DomainPredicateTypeResolver returning(final Class<?> javaType) {
        DomainPredicateTypeResolver domainOperationTypeResolver = RETURNING_JAVA_TYPE_CACHE.get(javaType);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new DomainPredicateTypeResolver() {
                @Override
                public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
                    return domainModel.getType(javaType);
                }
            };
            RETURNING_JAVA_TYPE_CACHE.put(javaType, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }
}
