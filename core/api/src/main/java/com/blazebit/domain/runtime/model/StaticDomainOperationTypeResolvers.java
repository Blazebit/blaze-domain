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
 * A domain operation type resolver utility that caches static resolvers.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class StaticDomainOperationTypeResolvers {

    private static final Map<String, DomainOperationTypeResolver> RETURNING_TYPE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Map<ArrayCacheKey<String>, DomainOperationTypeResolver> WIDEST_TYPE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Map<RestrictedCacheKey<String>, DomainOperationTypeResolver> RESTRICTED_TYPE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Map<Class<?>, DomainOperationTypeResolver> RETURNING_JAVA_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<ArrayCacheKey<Class<?>>, DomainOperationTypeResolver> WIDEST_JAVA_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<RestrictedCacheKey<Class<?>>, DomainOperationTypeResolver> RESTRICTED_JAVA_TYPE_CACHE = new ConcurrentHashMap<>();

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
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #returning(String)} instead
     */
    @Deprecated
    public static DomainOperationTypeResolver returning(final Class<?> javaType) {
        DomainOperationTypeResolver domainOperationTypeResolver = RETURNING_JAVA_TYPE_CACHE.get(javaType);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new ReturningJavaTypeDomainOperationTypeResolver(javaType);
            RETURNING_JAVA_TYPE_CACHE.put(javaType, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * Returns a domain operation type resolver that returns the domain type with the given type name.
     * If the arguments for a operation are none of the supported types, the operation type resolver will throw an
     * {@link IllegalArgumentException}.
     *
     * @param returningTypeName The domain type name that a operation produces
     * @param supportedTypeNames The domain type names that are supported for a operation
     * @return the domain operation type resolver
     * @since 1.0.12
     */
    public static DomainOperationTypeResolver returning(final String returningTypeName, final String... supportedTypeNames) {
        RestrictedCacheKey<String> key = new RestrictedCacheKey<>(returningTypeName, supportedTypeNames);
        DomainOperationTypeResolver domainOperationTypeResolver = RESTRICTED_TYPE_NAME_CACHE.get(key);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new RestrictedTypeDomainOperationTypeResolver(returningTypeName, supportedTypeNames);
            RESTRICTED_TYPE_NAME_CACHE.put(key, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * Returns a domain operation type resolver that returns the domain type with the given java type.
     * If the arguments for a operation are none of the supported types, the operation type resolver will throw an
     * {@link IllegalArgumentException}.
     *
     * @param returningJavaType The domain java type that a operation produces
     * @param supportedJavaTypes The domain java types that are supported for a operation
     * @return the domain operation type resolver
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #returning(String, String...)} instead
     */
    @Deprecated
    public static DomainOperationTypeResolver returning(final Class<?> returningJavaType, final Class<?>... supportedJavaTypes) {
        RestrictedCacheKey<Class<?>> key = new RestrictedCacheKey<>(returningJavaType, supportedJavaTypes);
        DomainOperationTypeResolver domainOperationTypeResolver = RESTRICTED_JAVA_TYPE_CACHE.get(key);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new RestrictedJavaTypeDomainOperationTypeResolver(returningJavaType, supportedJavaTypes);
            RESTRICTED_JAVA_TYPE_CACHE.put(key, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * Returns a domain operation type resolver that returns the domain type with one of the given type names,
     * preferring "smaller" types with lower indices. A "wider" type is chosen if one of the operator arguments
     * has a wider type.
     *
     * @param typeNames The domain type names from small to wide
     * @return the domain operation type resolver
     * @since 1.0.12
     */
    public static DomainOperationTypeResolver widest(final String... typeNames) {
        ArrayCacheKey<String> key = new ArrayCacheKey<>(typeNames);
        DomainOperationTypeResolver domainOperationTypeResolver = WIDEST_TYPE_NAME_CACHE.get(key);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new WidestTypeDomainOperationTypeResolver(typeNames);
            WIDEST_TYPE_NAME_CACHE.put(key, domainOperationTypeResolver);
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
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #widest(String...)} instead
     */
    @Deprecated
    public static DomainOperationTypeResolver widest(final Class<?>... javaTypes) {
        ArrayCacheKey<Class<?>> key = new ArrayCacheKey<>(javaTypes);
        DomainOperationTypeResolver domainOperationTypeResolver = WIDEST_JAVA_TYPE_CACHE.get(key);
        if (domainOperationTypeResolver == null) {
            domainOperationTypeResolver = new WidestJavaTypeDomainOperationTypeResolver(javaTypes);
            WIDEST_JAVA_TYPE_CACHE.put(key, domainOperationTypeResolver);
        }
        return domainOperationTypeResolver;
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class RestrictedJavaTypeDomainOperationTypeResolver implements DomainOperationTypeResolver, DomainSerializer<DomainOperationTypeResolver>, Serializable {

        private final Class<?> returningType;
        private final Set<Class<?>> supportedJavaTypes;

        public RestrictedJavaTypeDomainOperationTypeResolver(Class<?> returningType, Class<?>... supportedJavaTypes) {
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
                    throw new DomainTypeResolverException("The operation operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following: " + types);
                }
            }
            return domainModel.getType(returningType);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainOperationTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"RestrictedDomainOperationTypeResolver\":[");
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
    private static class WidestJavaTypeDomainOperationTypeResolver implements DomainOperationTypeResolver, DomainSerializer<DomainOperationTypeResolver>, Serializable {

        private final Class<?>[] javaTypes;

        public WidestJavaTypeDomainOperationTypeResolver(Class<?>... javaTypes) {
            this.javaTypes = javaTypes;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            List<DomainType> preferredTypes = new ArrayList<>(javaTypes.length);
            for (Class<?> javaType : javaTypes) {
                preferredTypes.add(domainModel.getType(javaType));
            }
            int typeIndex = Integer.MAX_VALUE;
            for (int i = 0; i < domainTypes.size(); i++) {
                DomainType domainType = domainTypes.get(i);
                int idx = preferredTypes.indexOf(domainType);
                if (idx == -1) {
                    throw new DomainTypeResolverException("The operation operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following types: " + preferredTypes);
                }
                typeIndex = Math.min(typeIndex, idx);
            }

            if (typeIndex == Integer.MAX_VALUE) {
                return preferredTypes.get(0);
            } else {
                return preferredTypes.get(typeIndex);
            }
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainOperationTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"WidestDomainOperationTypeResolver\":[[");
            for (Class<?> javaType : javaTypes) {
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
     * @since 1.0.12
     */
    private static class RestrictedTypeDomainOperationTypeResolver implements DomainOperationTypeResolver, DomainSerializer<DomainOperationTypeResolver>, Serializable {

        private final String returningTypeName;
        private final Set<String> supportedTypeNames;

        public RestrictedTypeDomainOperationTypeResolver(String returningTypeName, String... supportedTypeNames) {
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
                    throw new DomainTypeResolverException("The operation operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following: " + types);
                }
            }
            return domainModel.getType(returningTypeName);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainOperationTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"RestrictedDomainOperationTypeResolver\":[");
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
     * @since 1.0.12
     */
    private static class WidestTypeDomainOperationTypeResolver implements DomainOperationTypeResolver, DomainSerializer<DomainOperationTypeResolver>, Serializable {

        private final String[] typeNames;

        public WidestTypeDomainOperationTypeResolver(String... typeNames) {
            this.typeNames = typeNames;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, List<DomainType> domainTypes) {
            List<DomainType> preferredTypes = new ArrayList<>(typeNames.length);
            for (String typeName : typeNames) {
                preferredTypes.add(domainModel.getType(typeName));
            }
            int typeIndex = Integer.MAX_VALUE;
            for (int i = 0; i < domainTypes.size(); i++) {
                DomainType domainType = domainTypes.get(i);
                int idx = preferredTypes.indexOf(domainType);
                if (idx == -1) {
                    throw new DomainTypeResolverException("The operation operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following types: " + preferredTypes);
                }
                typeIndex = Math.min(typeIndex, idx);
            }

            if (typeIndex == Integer.MAX_VALUE) {
                return preferredTypes.get(0);
            } else {
                return preferredTypes.get(typeIndex);
            }
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainOperationTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"WidestDomainOperationTypeResolver\":[[");
            for (String typeName : typeNames) {
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
