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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A domain predicate type resolver utility that caches static resolvers.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class StaticDomainPredicateTypeResolvers {

    private static final Map<String, DomainPredicateTypeResolver> RETURNING_TYPE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, DomainPredicateTypeResolver> RETURNING_JAVA_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<RestrictedCacheKey<Class<?>>, DomainPredicateTypeResolver> RESTRICTED_JAVA_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<RestrictedCacheKey<String>, DomainPredicateTypeResolver> RESTRICTED_TYPE_NAME_CACHE = new ConcurrentHashMap<>();

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
     * Returns a domain predicate type resolver that returns the domain type with the given type name.
     * If the arguments for a predicate are none of the supported types, the predicate type resolver will throw an
     * {@link IllegalArgumentException}.
     *
     * @param returningTypeName The domain type name that a predicate produces
     * @param supportedTypeNames The domain type names that are supported for a predicate
     * @return the domain predicate type resolver
     */
    public static DomainPredicateTypeResolver returning(final String returningTypeName, final String... supportedTypeNames) {
        RestrictedCacheKey<String> key = new RestrictedCacheKey<>(returningTypeName, supportedTypeNames);
        DomainPredicateTypeResolver domainPredicateTypeResolver = RESTRICTED_TYPE_NAME_CACHE.get(key);
        if (domainPredicateTypeResolver == null) {
            domainPredicateTypeResolver = new RestrictedTypeDomainPredicateTypeResolver(returningTypeName, supportedTypeNames);
            RESTRICTED_TYPE_NAME_CACHE.put(key, domainPredicateTypeResolver);
        }
        return domainPredicateTypeResolver;
    }

    /**
     * Returns a domain predicate type resolver that returns the domain type with the given java type.
     * If the arguments for a predicate are none of the supported types, the predicate type resolver will throw an
     * {@link IllegalArgumentException}.
     *
     * @param returningJavaType The domain java type that a predicate produces
     * @param supportedJavaTypes The domain java types that are supported for a predicate
     * @return the domain predicate type resolver
     */
    public static DomainPredicateTypeResolver returning(final Class<?> returningJavaType, final Class<?>... supportedJavaTypes) {
        RestrictedCacheKey<Class<?>> key = new RestrictedCacheKey<>(returningJavaType, supportedJavaTypes);
        DomainPredicateTypeResolver domainPredicateTypeResolver = RESTRICTED_JAVA_TYPE_CACHE.get(key);
        if (domainPredicateTypeResolver == null) {
            domainPredicateTypeResolver = new RestrictedJavaTypeDomainPredicateTypeResolver(returningJavaType, supportedJavaTypes);
            RESTRICTED_JAVA_TYPE_CACHE.put(key, domainPredicateTypeResolver);
        }
        return domainPredicateTypeResolver;
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class RestrictedJavaTypeDomainPredicateTypeResolver implements DomainPredicateTypeResolver, DomainSerializer<DomainPredicateTypeResolver>, Serializable {

        private final Class<?> returningType;
        private final Set<Class<?>> supportedJavaTypes;

        public RestrictedJavaTypeDomainPredicateTypeResolver(Class<?> returningType, Class<?>... supportedJavaTypes) {
            this.returningType = returningType;
            this.supportedJavaTypes = new HashSet<>(Arrays.asList(supportedJavaTypes));
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            for (int i = 0; i < domainTypes.size(); i++) {
                DomainType domainType = domainTypes.get(i);
                if (!supportedJavaTypes.contains(domainType.getJavaType())) {
                    List<DomainType> types = new ArrayList<>(supportedJavaTypes.size());
                    for (Class<?> javaType : supportedJavaTypes) {
                        types.add(domainModel.getType(javaType));
                    }
                    throw new DomainTypeResolverException("The predicate operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following types: " + types);
                }
            }
            return domainModel.getType(returningType);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainPredicateTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"RestrictedDomainPredicateTypeResolver\":[");
            sb.append('"').append(domainModel.getType(returningType).getName()).append("\",[");
            for (Class<?> javaType : supportedJavaTypes) {
                sb.append('"').append(domainModel.getType(javaType).getName()).append("\",");
            }
            sb.setCharAt(sb.length() - 1, ']');
            sb.append(']').append('}');
            return (T) sb.toString();
        }
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
    private static class RestrictedTypeDomainPredicateTypeResolver implements DomainPredicateTypeResolver, DomainSerializer<DomainPredicateTypeResolver>, Serializable {

        private final String returningTypeName;
        private final Set<String> supportedTypeNames;

        public RestrictedTypeDomainPredicateTypeResolver(String returningTypeName, String... supportedTypeNames) {
            this.returningTypeName = returningTypeName;
            this.supportedTypeNames = new HashSet<>(Arrays.asList(supportedTypeNames));
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            for (int i = 0; i < domainTypes.size(); i++) {
                DomainType domainType = domainTypes.get(i);
                if (!supportedTypeNames.contains(domainType.getName())) {
                    List<DomainType> types = new ArrayList<>(supportedTypeNames.size());
                    for (String typeName : supportedTypeNames) {
                        types.add(domainModel.getType(typeName));
                    }
                    throw new DomainTypeResolverException("The predicate operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following types: " + types);
                }
            }
            return domainModel.getType(returningTypeName);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainPredicateTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"RestrictedDomainPredicateTypeResolver\":[");
            sb.append('"').append(returningTypeName).append("\",[");
            for (String typeName : supportedTypeNames) {
                sb.append('"').append(typeName).append("\",");
            }
            sb.setCharAt(sb.length() - 1, ']');
            sb.append(']').append('}');
            return (T) sb.toString();
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
