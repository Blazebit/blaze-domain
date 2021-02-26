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

import com.blazebit.domain.spi.DomainSerializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A domain function type resolver utility that caches static resolvers.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class StaticDomainFunctionTypeResolvers {

    /**
     * A domain function type resolver that always resolves to the first argument type.
     */
    public static final DomainFunctionTypeResolver FIRST_ARGUMENT_TYPE = new FirstArgumentDomainFunctionTypeResolver();

    /**
     * A domain function type resolver that always resolves to the static domain function return type.
     */
    public static final DomainFunctionTypeResolver STATIC_RETURN_TYPE = new StaticDomainFunctionTypeResolver();

    private static final Map<String, DomainFunctionTypeResolver> RETURNING_TYPE_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Map<ArrayCacheKey<String>, DomainFunctionTypeResolver> WIDEST_TYPE_NAME_CACHE = new ConcurrentHashMap<>();

    private StaticDomainFunctionTypeResolvers() {
    }

    /**
     * Returns a domain function type resolver that always returns the domain type with the given type name.
     *
     * @param typeName The static domain type name
     * @return the domain function type resolver
     */
    public static DomainFunctionTypeResolver returning(final String typeName) {
        DomainFunctionTypeResolver domainFunctionTypeResolver = RETURNING_TYPE_NAME_CACHE.get(typeName);
        if (domainFunctionTypeResolver == null) {
            domainFunctionTypeResolver = new ReturningTypeDomainFunctionTypeResolver(typeName);
            RETURNING_TYPE_NAME_CACHE.put(typeName, domainFunctionTypeResolver);
        }
        return domainFunctionTypeResolver;
    }

    /**
     * Returns a domain function type resolver that returns the domain type with one of the given type names,
     * preferring "smaller" types with lower indices. A "wider" type is chosen if one of the operator arguments
     * has a wider type.
     *
     * @param typeNames The domain type names from small to wide
     * @return the domain function type resolver
     * @since 1.0.12
     */
    public static DomainFunctionTypeResolver widest(final String... typeNames) {
        ArrayCacheKey<String> key = new ArrayCacheKey<>(typeNames);
        DomainFunctionTypeResolver domainFunctionTypeResolver = WIDEST_TYPE_NAME_CACHE.get(key);
        if (domainFunctionTypeResolver == null) {
            domainFunctionTypeResolver = new WidestDomainFunctionTypeResolver(typeNames);
            WIDEST_TYPE_NAME_CACHE.put(key, domainFunctionTypeResolver);
        }
        return domainFunctionTypeResolver;
    }

    private static void validateArgumentTypes(Map<DomainFunctionArgument, DomainType> argumentTypes) {
        for (Map.Entry<DomainFunctionArgument, DomainType> entry : argumentTypes.entrySet()) {
            DomainFunctionArgument functionArgument = entry.getKey();
            if (functionArgument.getType() == null || entry.getValue() == null) {
                continue;
            }
            if (functionArgument.getType() instanceof CollectionDomainType && entry.getValue() instanceof CollectionDomainType) {
                if (((CollectionDomainType) functionArgument.getType()).getElementType() == null || ((CollectionDomainType) entry.getValue()).getElementType() == null) {
                    continue;
                }
            }
            if (functionArgument.getType() != entry.getValue()) {
                throw new DomainTypeResolverException("Unsupported argument type '" + entry.getValue() + "' for argument '" + functionArgument + "' of function '" + functionArgument.getOwner().getName() + "'! Expected type: " + functionArgument.getType());
            }
        }
    }

    /**
     * @author Christian Beikov
     * @since 1.0.12
     */
    private static class WidestDomainFunctionTypeResolver implements DomainFunctionTypeResolver, DomainSerializer<DomainFunctionTypeResolver>, Serializable {

        private final String[] typeNames;

        public WidestDomainFunctionTypeResolver(String... typeNames) {
            this.typeNames = typeNames;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, DomainFunction function, Map<DomainFunctionArgument, DomainType> argumentTypes) {
            int typeIndex = typeNames.length;
            DomainType domainType = null;
            for (Map.Entry<DomainFunctionArgument, DomainType> entry : argumentTypes.entrySet()) {
                DomainType type = entry.getValue();
                String typeName = type.getName();
                for (int i = 0; i < typeIndex; i++) {
                    if (typeName.equals(typeNames[i])) {
                        typeIndex = i;
                        domainType = type;
                        break;
                    }
                }
                if (typeIndex == 0) {
                    break;
                } else if (typeIndex == typeNames.length) {
                    List<DomainType> preferredTypes = new ArrayList<>(typeNames.length);
                    for (String name : typeNames) {
                        preferredTypes.add(domainModel.getType(name));
                    }

                    throw new DomainTypeResolverException("Unsupported argument type '" + type + "' for argument '" + entry.getKey() + "' of function '" + function.getName() + "'! Expected one of the following types: " + preferredTypes);
                }
            }

            if (typeIndex == Integer.MAX_VALUE) {
                return domainModel.getType(typeNames[0]);
            } else {
                return domainType;
            }
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainFunctionTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            sb.append("{\"WidestDomainFunctionTypeResolver\":[[");
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
    private static class ReturningTypeDomainFunctionTypeResolver implements DomainFunctionTypeResolver, DomainSerializer<DomainFunctionTypeResolver>, Serializable {

        private final String typeName;

        public ReturningTypeDomainFunctionTypeResolver(String typeName) {
            this.typeName = typeName;
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, DomainFunction function, Map<DomainFunctionArgument, DomainType> argumentTypes) {
            validateArgumentTypes(argumentTypes);
            return domainModel.getType(typeName);
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainFunctionTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            return (T) ("{\"FixedDomainFunctionTypeResolver\":[\"" + typeName + "\"]}");
        }
    }

    /**
     * A resolver that returns the first argument type as result type.
     *
     * @author Christian Beikov
     * @since 1.0.0
     */
    public static class FirstArgumentDomainFunctionTypeResolver implements DomainFunctionTypeResolver, DomainSerializer<DomainFunctionTypeResolver>, Serializable {

        @Override
        public DomainType resolveType(DomainModel domainModel, DomainFunction function, Map<DomainFunctionArgument, DomainType> argumentTypes) {
            validateArgumentTypes(argumentTypes);
            for (DomainFunctionArgument argument : function.getArguments()) {
                DomainType domainType = argumentTypes.get(argument);
                if (domainType != null) {
                    return domainType;
                }
            }

            return null;
        }

        @Override
        public <T> T serialize(DomainModel domainModel, DomainFunctionTypeResolver element, Class<T> targetType, String format, Map<String, Object> properties) {
            if (targetType != String.class || !"json".equals(format)) {
                return null;
            }
            return (T) "\"FirstArgumentDomainFunctionTypeResolver\"";
        }
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class StaticDomainFunctionTypeResolver implements DomainFunctionTypeResolver, Serializable {

        private StaticDomainFunctionTypeResolver() {
        }

        @Override
        public DomainType resolveType(DomainModel domainModel, DomainFunction function, Map<DomainFunctionArgument, DomainType> argumentTypes) {
            validateArgumentTypes(argumentTypes);
            return function.getResultType();
        }
    }

}
