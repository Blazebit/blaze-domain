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

import com.blazebit.domain.spi.DomainSerializer;

import java.io.Serializable;
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
            domainOperationTypeResolver = new ReturningTypeDomainPredicateTypeResolver(typeName);
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
            domainOperationTypeResolver = new ReturningJavaTypeDomainPredicateTypeResolver(javaType);
            RETURNING_JAVA_TYPE_CACHE.put(javaType, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class ReturningJavaTypeDomainPredicateTypeResolver implements DomainPredicateTypeResolver, DomainSerializer<DomainPredicateTypeResolver>, Serializable {

        private final Class<?> javaType;

        public ReturningJavaTypeDomainPredicateTypeResolver(Class<?> javaType) {
            this.javaType = javaType;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            return domainModel.getType(javaType);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainPredicateTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            return (T) ("{\"FixedDomainPredicateTypeResolver\":[\"" + domainModel.getType(javaType).getName() + "\"]}");
        }
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class ReturningTypeDomainPredicateTypeResolver implements DomainPredicateTypeResolver, DomainSerializer<DomainPredicateTypeResolver>, Serializable {

        private final String typeName;

        public ReturningTypeDomainPredicateTypeResolver(String typeName) {
            this.typeName = typeName;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            return domainModel.getType(typeName);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainPredicateTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            return (T) ("{\"FixedDomainPredicateTypeResolver\":[\"" + typeName + "\"]}");
        }
    }
}
