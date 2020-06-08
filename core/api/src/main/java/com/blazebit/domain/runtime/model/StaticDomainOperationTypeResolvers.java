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
            domainOperationTypeResolver = new ReturningTypeDomainOperationTypeResolver(typeName);
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
            domainOperationTypeResolver = new ReturningJavaTypeDomainOperationTypeResolver(javaType);
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
            domainOperationTypeResolver = new WidestDomainOperationTypeResolver(javaTypes);
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
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class WidestDomainOperationTypeResolver implements DomainOperationTypeResolver, DomainSerializer<DomainOperationTypeResolver>, Serializable {

        private final Class<?>[] javaTypes;

        public WidestDomainOperationTypeResolver(Class<?>... javaTypes) {
            this.javaTypes = javaTypes;
        }

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

        @Override
        public <T> T serialize(DomainModel domainModel, DomainOperationTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"WidestDomainOperationTypeResolver\":[");
            for (Class<?> javaType : javaTypes) {
                sb.append('"').append(domainModel.getType(javaType).getName()).append("\",");
            }
            sb.setCharAt(sb.length() - 1, ']');
            sb.append('}');
            return (T) sb.toString();
        }
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class ReturningJavaTypeDomainOperationTypeResolver implements DomainOperationTypeResolver, DomainSerializer<DomainOperationTypeResolver>, Serializable {

        private final Class<?> javaType;

        public ReturningJavaTypeDomainOperationTypeResolver(Class<?> javaType) {
            this.javaType = javaType;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            return domainModel.getType(javaType);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainOperationTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            return (T) ("{\"FixedDomainOperationTypeResolver\":[\"" + domainModel.getType(javaType).getName() + "\"]}");
        }
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class ReturningTypeDomainOperationTypeResolver implements DomainOperationTypeResolver, DomainSerializer<DomainOperationTypeResolver>, Serializable {

        private final String typeName;

        public ReturningTypeDomainOperationTypeResolver(String typeName) {
            this.typeName = typeName;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            return domainModel.getType(typeName);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainOperationTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            return (T) ("{\"FixedDomainOperationTypeResolver\":[\"" + typeName + "\"]}");
        }
    }
}
