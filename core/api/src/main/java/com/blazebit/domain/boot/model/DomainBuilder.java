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

package com.blazebit.domain.boot.model;

import com.blazebit.domain.runtime.model.DomainFunctionTypeResolver;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainOperationTypeResolver;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicateType;
import com.blazebit.domain.runtime.model.DomainPredicateTypeResolver;
import com.blazebit.domain.runtime.model.EntityLiteralResolver;
import com.blazebit.domain.runtime.model.EnumLiteralResolver;
import com.blazebit.domain.runtime.model.NumericLiteralResolver;
import com.blazebit.domain.runtime.model.StringLiteralResolver;
import com.blazebit.domain.runtime.model.TemporalLiteralResolver;
import com.blazebit.domain.runtime.model.BooleanLiteralResolver;
import com.blazebit.domain.runtime.model.CollectionLiteralResolver;

/**
 * A builder for a domain model.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainBuilder {

    /**
     * Adds the given boolean literal resolver.
     *
     * @param literalResolver The boolean literal resolver to add
     * @return this for chaining
     */
    public DomainBuilder withBooleanLiteralResolver(BooleanLiteralResolver literalResolver);

    /**
     * Adds the given numeric literal resolver.
     *
     * @param literalResolver The numeric literal resolver to add
     * @return this for chaining
     */
    public DomainBuilder withNumericLiteralResolver(NumericLiteralResolver literalResolver);

    /**
     * Adds the given string literal resolver.
     *
     * @param literalResolver The string literal resolver to add
     * @return this for chaining
     */
    public DomainBuilder withStringLiteralResolver(StringLiteralResolver literalResolver);

    /**
     * Adds the given temporal literal resolver.
     *
     * @param literalResolver The temporal literal resolver to add
     * @return this for chaining
     */
    public DomainBuilder withTemporalLiteralResolver(TemporalLiteralResolver literalResolver);

    /**
     * Adds the given enum literal resolver.
     *
     * @param literalResolver The enum literal resolver to add
     * @return this for chaining
     */
    public DomainBuilder withEnumLiteralResolver(EnumLiteralResolver literalResolver);

    /**
     * Adds the given entity literal resolver.
     *
     * @param literalResolver The entity literal resolver to add
     * @return this for chaining
     */
    public DomainBuilder withEntityLiteralResolver(EntityLiteralResolver literalResolver);

    /**
     * Adds the given collection literal resolver.
     *
     * @param literalResolver The collection literal resolver to add
     * @return this for chaining
     */
    public DomainBuilder withCollectionLiteralResolver(CollectionLiteralResolver literalResolver);

    /**
     * Adds the given function type resolver for the given function name.
     *
     * @param functionName The function name for which to register the function type resolver
     * @param functionTypeResolver The function type resolver to register
     * @return this for chaining
     */
    public DomainBuilder withFunctionTypeResolver(String functionName, DomainFunctionTypeResolver functionTypeResolver);

    /**
     * Adds the given operation type resolver for the given type name and domain operator.
     *
     * @param typeName The type name of a domain type for which to register the operation type resolver
     * @param domainOperator The domain operator for which to register the operation type resolver
     * @param operationTypeResolver The operation type resolver to register
     * @return this for chaining
     */
    public DomainBuilder withOperationTypeResolver(String typeName, DomainOperator domainOperator, DomainOperationTypeResolver operationTypeResolver);

    /**
     * Adds the given operation type resolver for the given java type and domain operator.
     *
     * @param javaType The java type of a domain type for which to register the operation type resolver
     * @param domainOperator The domain operator for which to register the operation type resolver
     * @param operationTypeResolver The operation type resolver to register
     * @return this for chaining
     */
    public DomainBuilder withOperationTypeResolver(Class<?> javaType, DomainOperator domainOperator, DomainOperationTypeResolver operationTypeResolver);

    /**
     * Adds the given predicate type resolver for the given type name and domain predicate type.
     *
     * @param typeName The type name of a domain type for which to register the predicate type resolver
     * @param domainPredicateType The domain predicate for which to register the predicate type resolver
     * @param predicateTypeResolver The predicate type resolver to register
     * @return this for chaining
     */
    public DomainBuilder withPredicateTypeResolver(String typeName, DomainPredicateType domainPredicateType, DomainPredicateTypeResolver predicateTypeResolver);

    /**
     * Adds the given predicate type resolver for the given java type and domain predicate type.
     *
     * @param javaType The java type of a domain type for which to register the predicate type resolver
     * @param domainPredicateType The domain predicate for which to register the predicate type resolver
     * @param predicateTypeResolver The predicate type resolver to register
     * @return this for chaining
     */
    public DomainBuilder withPredicateTypeResolver(Class<?> javaType, DomainPredicateType domainPredicateType, DomainPredicateTypeResolver predicateTypeResolver);

    /**
     * Enables the given domain operator for the given type name.
     *
     * @param typeName The type name of a domain type for which to enable the given operator
     * @param operator The domain operator to enable
     * @return this for chaining
     */
    public DomainBuilder withOperator(String typeName, DomainOperator operator);

    /**
     * Enables the given domain operators for the given type name.
     *
     * @param typeName The type name of a domain type for which to enable the given operators
     * @param operators The domain operators to enable
     * @return this for chaining
     */
    public DomainBuilder withOperator(String typeName, DomainOperator... operators);

    /**
     * Enables the given domain predicate for the given type name.
     *
     * @param typeName The type name of a domain type for which to enable the given predicate
     * @param predicate The domain predicate to enable
     * @return this for chaining
     */
    public DomainBuilder withPredicate(String typeName, DomainPredicateType predicate);

    /**
     * Enables the given domain predicates for the given type name.
     *
     * @param typeName The type name of a domain type for which to enable the given predicates
     * @param predicates The domain predicates to enable
     * @return this for chaining
     */
    public DomainBuilder withPredicate(String typeName, DomainPredicateType... predicates);

    /**
     * Creates a builder for a domain function with the given name.
     *
     * @param name The function name
     * @return the domain function builder
     */
    public DomainFunctionBuilder createFunction(String name);

    /**
     * Creates a basic domain type with the given type name.
     *
     * @param name The type name
     * @return this for chaining
     */
    public DomainBuilder createBasicType(String name);

    /**
     * Creates a basic domain type with the given type name and Java type.
     *
     * @param name The type name
     * @param javaType The Java type
     * @return this for chaining
     */
    public DomainBuilder createBasicType(String name, Class<?> javaType);

    /**
     * Creates a basic domain type with the given type name and metadata definitions.
     *
     * @param name The type name
     * @param metadataDefinitions The metadata definitions for the basic type
     * @return this for chaining
     */
    public DomainBuilder createBasicType(String name, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Creates a basic domain type with the given type name, Java type and metadata definitions.
     *
     * @param name The type name
     * @param javaType The Java type
     * @param metadataDefinitions The metadata definitions for the basic type
     * @return this for chaining
     */
    public DomainBuilder createBasicType(String name, Class<?> javaType, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Creates an entity domain type with the given type name.
     *
     * @param name The type name
     * @return the entity domain builder
     */
    public EntityDomainTypeBuilder createEntityType(String name);

    /**
     * Creates an entity domain type with the given type name and Java type.
     *
     * @param name The type name
     * @param javaType The Java type
     * @return the entity domain builder
     */
    public EntityDomainTypeBuilder createEntityType(String name, Class<?> javaType);

    /**
     * Creates an enum domain type with the given type name.
     *
     * @param name The type name
     * @return the enum domain builder
     */
    public EnumDomainTypeBuilder createEnumType(String name);

    /**
     * Creates an enum domain type with the given type name and Java type.
     *
     * @param name The type name
     * @param javaType The Java type
     * @return the enum domain builder
     */
    public EnumDomainTypeBuilder createEnumType(String name, Class<? extends Enum<?>> javaType);

    /**
     * Sets whether function names are case sensitive.
     *
     * @param caseSensitive Whether function names are case sensitive
     * @return this for chaining
     */
    public DomainBuilder setFunctionCaseSensitive(boolean caseSensitive);

    /**
     * Builds and validates the domain model as defined via this builder.
     *
     * @return The domain model
     */
    public DomainModel build();
}
