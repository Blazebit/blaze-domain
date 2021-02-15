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

package com.blazebit.domain.boot.model;

import java.util.List;
import java.util.Map;

/**
 * A builder for a domain function.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainFunctionBuilder {

    /**
     * The name of the domain function.
     *
     * @return the name
     */
    public String getName();

    /**
     * The minimum argument count for the function.
     *
     * @return the minimum function argument count
     */
    public int getMinArgumentCount();

    /**
     * The maximum argument count for the function.
     *
     * @return the maximum function argument count
     */
    public int getArgumentCount();

    /**
     * The argument definitions for this domain function.
     *
     * @return the argument definitions
     */
    public List<DomainFunctionArgumentDefinition> getArgumentDefinitions();

    /**
     * Specifies the minimum argument count for the domain function.
     *
     * @param minArgumentCount The minimum argument count for the domain function
     * @return this for chaining
     */
    public DomainFunctionBuilder withMinArgumentCount(int minArgumentCount);

    /**
     * Specifies the exact argument count for the domain function.
     *
     * @param exactArgumentCount The exact argument count for the domain function
     * @return this for chaining
     */
    public DomainFunctionBuilder withExactArgumentCount(int exactArgumentCount);

    /**
     * Adds the argument with the given argument name as next function argument.
     *
     * @param name The argument name
     * @return this for chaining
     */
    public DomainFunctionBuilder withArgument(String name);

    /**
     * Adds the argument with the given argument name and metadata as next function argument.
     *
     * @param name The argument name
     * @param metadataDefinitions The metadata definitions for the argument
     * @return this for chaining
     */
    public DomainFunctionBuilder withArgument(String name, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds the argument with the given argument name and argument type name as next function argument.
     *
     * @param name The argument name
     * @param typeName The argument type name
     * @return this for chaining
     */
    public DomainFunctionBuilder withArgument(String name, String typeName);

    /**
     * Adds the argument with the given argument name and argument type name as well as metadata as next function argument.
     *
     * @param name The argument name
     * @param typeName The argument type name
     * @param metadataDefinitions The metadata definitions for the argument
     * @return this for chaining
     */
    public DomainFunctionBuilder withArgument(String name, String typeName, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds the argument with the given argument name and argument java type as next function argument.
     *
     * @param name The argument name
     * @param javaType The argument java type
     * @return this for chaining
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #withArgument(String, String)} instead
     */
    @Deprecated
    public DomainFunctionBuilder withArgument(String name, Class<?> javaType);

    /**
     * Adds the argument with the given argument name and argument java type as well as metadata as next function argument.
     *
     * @param name The argument name
     * @param javaType The argument java type
     * @param metadataDefinitions The metadata definitions for the argument
     * @return this for chaining
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #withArgument(String, String, MetadataDefinition[])} instead
     */
    @Deprecated
    public DomainFunctionBuilder withArgument(String name, Class<?> javaType, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds the collection argument with the given argument name as next function argument.
     *
     * @param name The argument name
     * @return this for chaining
     */
    public DomainFunctionBuilder withCollectionArgument(String name);

    /**
     * Adds the collection argument with the given argument name and metadata as next function argument.
     *
     * @param name The argument name
     * @param metadataDefinitions The metadata definitions for the argument
     * @return this for chaining
     */
    public DomainFunctionBuilder withCollectionArgument(String name, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds the collection argument with the given argument name and argument element type name as next function argument.
     *
     * @param name The argument name
     * @param typeName The argument element type name
     * @return this for chaining
     */
    public DomainFunctionBuilder withCollectionArgument(String name, String typeName);

    /**
     * Adds the collection argument with the given argument name and argument element type name as well as metadata as next function argument.
     *
     * @param name The argument name
     * @param typeName The argument element type name
     * @param metadataDefinitions The metadata definitions for the argument
     * @return this for chaining
     */
    public DomainFunctionBuilder withCollectionArgument(String name, String typeName, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds the collection argument with the given argument name and argument element java type as next function argument.
     *
     * @param name The argument name
     * @param javaType The argument element java type
     * @return this for chaining
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #withCollectionArgument(String, String)} instead
     */
    @Deprecated
    public DomainFunctionBuilder withCollectionArgument(String name, Class<?> javaType);

    /**
     * Adds the collection argument with the given argument name and argument element java type as well as metadata as next function argument.
     *
     * @param name The argument name
     * @param javaType The argument element java type
     * @param metadataDefinitions The metadata definitions for the argument
     * @return this for chaining
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #withCollectionArgument(String, String, MetadataDefinition...)} instead
     */
    @Deprecated
    public DomainFunctionBuilder withCollectionArgument(String name, Class<?> javaType, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Specifies that the function accepts the given type names as arguments in the given order.
     *
     * @param typeNames The ordered argument type names
     * @return this for chaining
     */
    public DomainFunctionBuilder withArgumentTypes(String... typeNames);

    /**
     * Specifies that the function accepts the given java types as arguments in the given order.
     *
     * @param typeNames The ordered argument java type
     * @return this for chaining
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #withArgumentTypes(String...)} instead
     */
    @Deprecated
    public DomainFunctionBuilder withArgumentTypes(Class<?>... typeNames);

    /**
     * Specifies that the function returns a fixed type as defined by the given type name.
     *
     * @param typeName The type name of the result type
     * @return this for chaining
     */
    public DomainFunctionBuilder withResultType(String typeName);

    /**
     * Specifies that the function returns a fixed type as defined by the given java type.
     *
     * @param javaType The java type of the result type
     * @return this for chaining
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #withResultType(String)} instead
     */
    @Deprecated
    public DomainFunctionBuilder withResultType(Class<?> javaType);

    /**
     * Specifies that the function returns a collection type.
     *
     * @return this for chaining
     */
    public DomainFunctionBuilder withCollectionResultType();

    /**
     * Specifies that the function returns a fixed collection type with the element type as defined by the given type name.
     *
     * @param typeName The collection element type name of the result type
     * @return this for chaining
     */
    public DomainFunctionBuilder withCollectionResultType(String typeName);

    /**
     * Specifies that the function returns a fixed collection type with the element type as defined by the given java type.
     *
     * @param javaType The collection element java type of the result type
     * @return this for chaining
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #withCollectionResultType(String)} instead
     */
    @Deprecated
    public DomainFunctionBuilder withCollectionResultType(Class<?> javaType);

    /**
     * Adds the given metadata definition to the function.
     *
     * @param metadataDefinition The metadata definition
     * @return this for chaining
     */
    public DomainFunctionBuilder withMetadata(MetadataDefinition<?> metadataDefinition);

    /**
     * Returns the metadata definitions of this domain element.
     *
     * @return the metadata definitions
     */
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions();

    /**
     * Builds and adds the domain function to the domain builder.
     *
     * @return the domain builder for chaining
     */
    public DomainBuilder build();

}
