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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A domain operation type resolver utility that caches static resolvers.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class StaticDomainOperationTypeResolvers {

    private static final Map<String, DomainOperationTypeResolver> RETURNING_TYPE_NAME_CACHE = new HashMap<>();
    private static final Map<Class<?>, DomainOperationTypeResolver> RETURNING_JAVA_TYPE_CACHE = new HashMap<>();
    private static final Map<ClassArray, DomainOperationTypeResolver> WIDEST_CACHE = new HashMap<>();

    private StaticDomainOperationTypeResolvers() {
    }

    /**
     * Returns a domain operation type resolver that always returns the domain type with the given type name.
     *
     * @param typeName The static domain type name
     * @return the domain operation type resolver
     */
    public static DomainOperationTypeResolver returning(final String typeName) {
        DomainOperationTypeResolver domainOperationTypeResolver = RETURNING_TYPE_NAME_CACHE.get(typeName);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new SerializableDomainOperationTypeResolver() {
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
     * Returns a domain operation type resolver that always returns the domain type with the given java type.
     *
     * @param javaType The static domain java type
     * @return the domain operation type resolver
     */
    public static DomainOperationTypeResolver returning(final Class<?> javaType) {
        DomainOperationTypeResolver domainOperationTypeResolver = RETURNING_JAVA_TYPE_CACHE.get(javaType);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new SerializableDomainOperationTypeResolver() {
                @Override
                public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
                    return domainModel.getType(javaType);
                }
            };
            RETURNING_JAVA_TYPE_CACHE.put(javaType, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * Returns a domain operation type resolver that returns the domain type with one of the given java types,
     * preferring "smaller" types with lower indices. A "wider" type is chosen if one of the operator arguments
     * has a wider type.
     *
     * @param javaTypes The domain java types from small to wide
     * @return the domain operation type resolver
     */
    public static DomainOperationTypeResolver widest(final Class<?>... javaTypes) {
        ClassArray key = new ClassArray(javaTypes);
        DomainOperationTypeResolver domainOperationTypeResolver = WIDEST_CACHE.get(key);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new SerializableDomainOperationTypeResolver() {
                @Override
                public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
                    List<DomainType> preferredTypes = new ArrayList<>(javaTypes.length);
                    for (Class<?> javaType : javaTypes) {
                        preferredTypes.add(domainModel.getType(javaType));
                    }
                    for (DomainType preferredType : preferredTypes) {
                        if (domainTypes.contains(preferredType)) {
                            return preferredType;
                        }
                    }

                    return domainTypes.isEmpty() ? preferredTypes.get(0) : domainTypes.get(0);
                }
            };
            WIDEST_CACHE.put(key, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class ClassArray {

        private final Class<?>[] classes;

        private ClassArray(Class<?>[] classes) {
            this.classes = classes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            return Arrays.equals(classes, ((ClassArray) o).classes);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(classes);
        }
    }

    /**
     * A serializable version.
     *
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static interface SerializableDomainOperationTypeResolver extends DomainOperationTypeResolver, Serializable {
    }
}
