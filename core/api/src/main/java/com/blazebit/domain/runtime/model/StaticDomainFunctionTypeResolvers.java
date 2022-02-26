/*
 * Copyright 2019 - 2022 Blazebit.
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
     * A domain function type resolver that always resolves to the second argument type.
     */
    public static final DomainFunctionTypeResolver SECOND_ARGUMENT_TYPE = new SecondArgumentDomainFunctionTypeResolver();

    /**
     * A domain function type resolver that always resolves to the third argument type.
     */
    public static final DomainFunctionTypeResolver THIRD_ARGUMENT_TYPE = new ThirdArgumentDomainFunctionTypeResolver();

    /**
     * A domain function type resolver that always resolves to the fourth argument type.
     */
    public static final DomainFunctionTypeResolver FOURTH_ARGUMENT_TYPE = new FourthArgumentDomainFunctionTypeResolver();

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

    /**
     * A domain function type resolver that always resolves to the nth argument type.
     * @param index The 0-based parameter index
     * @return the domain function type resolver
     * @since 2.0.3
     */
    public static DomainFunctionTypeResolver nthArgument(int index) {
        return new NthArgumentDomainFunctionTypeResolver(index);
    }

    private static void validateArgumentTypes(Map<DomainFunctionArgument, DomainType> argumentTypes) {
        OUTER: for (Map.Entry<DomainFunctionArgument, DomainType> entry : argumentTypes.entrySet()) {
            DomainFunctionArgument functionArgument = entry.getKey();
            DomainType argumentType = functionArgument.getType();
            if (argumentType == null || entry.getValue() == null) {
                continue;
            }
            if (argumentType instanceof CollectionDomainType && entry.getValue() instanceof CollectionDomainType) {
                if (((CollectionDomainType) argumentType).getElementType() == null || ((CollectionDomainType) entry.getValue()).getElementType() == null) {
                    continue;
                }
            }
            if (argumentType instanceof UnionDomainType) {
                if (entry.getValue() instanceof CollectionDomainType) {
                    for (DomainType unionElement : ((UnionDomainType) argumentType).getUnionElements()) {
                        if (unionElement == entry.getValue() || unionElement instanceof CollectionDomainType && ((CollectionDomainType) unionElement).getElementType() == null) {
                            continue OUTER;
                        }
                    }
                } else {
                    for (DomainType unionElement : ((UnionDomainType) argumentType).getUnionElements()) {
                        if (unionElement == entry.getValue()) {
                            continue OUTER;
                        }
                    }
                }
            }
            if (argumentType != entry.getValue()) {
                throw new DomainTypeResolverException("Unsupported argument type '" + entry.getValue() + "' for argument '" + functionArgument + "' of function '" + functionArgument.getOwner().getName() + "'! Expected type: " + argumentType);
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
     * This class is useful for the use with annotations e.g. {@code @DomainFunction(typeResolver = FirstArgumentDomainFunctionTypeResolver.class)}.
     *
     * @author Christian Beikov
     * @since 2.0.3
     */
    public static class FirstArgumentDomainFunctionTypeResolver extends NthArgumentDomainFunctionTypeResolver {
        /**
         * Creates a new domain function type resolver that resolves the result type to first argument type.
         */
        public FirstArgumentDomainFunctionTypeResolver() {
            super(0);
        }
    }

    /**
     * A resolver that returns the second argument type as result type.
     *
     * This class is useful for the use with annotations e.g. {@code @DomainFunction(typeResolver = SecondArgumentDomainFunctionTypeResolver.class)}.
     *
     * @author Christian Beikov
     * @since 2.0.3
     */
    public static class SecondArgumentDomainFunctionTypeResolver extends NthArgumentDomainFunctionTypeResolver {
        /**
         * Creates a new domain function type resolver that resolves the result type to second argument type.
         */
        public SecondArgumentDomainFunctionTypeResolver() {
            super(1);
        }
    }

    /**
     * A resolver that returns the third argument type as result type.
     *
     * This class is useful for the use with annotations e.g. {@code @DomainFunction(typeResolver = ThirdArgumentDomainFunctionTypeResolver.class)}.
     *
     * @author Christian Beikov
     * @since 2.0.3
     */
    public static class ThirdArgumentDomainFunctionTypeResolver extends NthArgumentDomainFunctionTypeResolver {
        /**
         * Creates a new domain function type resolver that resolves the result type to third argument type.
         */
        public ThirdArgumentDomainFunctionTypeResolver() {
            super(2);
        }
    }

    /**
     * A resolver that returns the fourth argument type as result type.
     *
     * This class is useful for the use with annotations e.g. {@code @DomainFunction(typeResolver = FourthArgumentDomainFunctionTypeResolver.class)}.
     *
     * @author Christian Beikov
     * @since 2.0.3
     */
    public static class FourthArgumentDomainFunctionTypeResolver extends NthArgumentDomainFunctionTypeResolver {
        /**
         * Creates a new domain function type resolver that resolves the result type to fourth argument type.
         */
        public FourthArgumentDomainFunctionTypeResolver() {
            super(3);
        }
    }

    /**
     * A resolver that returns the nth argument type as result type.
     *
     * @author Christian Beikov
     * @since 2.0.3
     */
    public static class NthArgumentDomainFunctionTypeResolver implements DomainFunctionTypeResolver, DomainSerializer<DomainFunctionTypeResolver>, Serializable {

        private final int index;

        /**
         * Creates a new domain function type resolver that resolves the result type to the argument type at the given index.
         * @param index The argument index of the argument type to resolve the result type to
         */
        public NthArgumentDomainFunctionTypeResolver(int index) {
            this.index = index;
        }

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
            return (T) ("{\"NthArgumentDomainFunctionTypeResolver\":[" + index + "]}");
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
