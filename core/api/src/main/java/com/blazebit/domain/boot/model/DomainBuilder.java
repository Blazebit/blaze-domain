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

package com.blazebit.domain.boot.model;

import com.blazebit.domain.runtime.model.DomainFunctionTypeResolver;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainOperationTypeResolver;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.runtime.model.DomainPredicateTypeResolver;
import com.blazebit.domain.spi.DomainSerializer;
import com.blazebit.domain.spi.ServiceProvider;

import java.util.Map;
import java.util.Set;

/**
 * A builder for a domain model.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainBuilder extends ServiceProvider {

    /**
     * Loads the default settings in this domain builder.
     *
     * @return this for chaining
     */
    public DomainBuilder withDefaults();

    /**
     * Sets the given type name as default type to use for predicate results.
     *
     * @param typeName The type name to use
     * @return this for chaining
     */
    public DomainBuilder withDefaultPredicateResultType(String typeName);

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
     * Adds the given predicate type resolver for the given type name and domain predicate type.
     *
     * @param typeName The type name of a domain type for which to register the predicate type resolver
     * @param domainPredicate The domain predicate for which to register the predicate type resolver
     * @param predicateTypeResolver The predicate type resolver to register
     * @return this for chaining
     */
    public DomainBuilder withPredicateTypeResolver(String typeName, DomainPredicate domainPredicate, DomainPredicateTypeResolver predicateTypeResolver);

    /**
     * Returns the domain operation type resolver for the given type name and domain operator.
     *
     * @param typeName The type name
     * @param domainOperator The domain operator
     * @return The operation type resolver
     * @since 1.0.12
     */
    public DomainOperationTypeResolver getOperationTypeResolver(String typeName, DomainOperator domainOperator);

    /**
     * Returns the domain predicate type resolver for the given type name and domain predicate.
     *
     * @param typeName The type name
     * @param domainPredicate The domain predicate
     * @return The predicate type resolver
     * @since 1.0.12
     */
    public DomainPredicateTypeResolver getPredicateTypeResolver(String typeName, DomainPredicate domainPredicate);

    /**
     * Adds the given domain model serializer.
     *
     * @param serializer The domain model serializer
     * @return this for chaining
     */
    public DomainBuilder withSerializer(DomainSerializer<?> serializer);

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
    public DomainBuilder withPredicate(String typeName, DomainPredicate predicate);

    /**
     * Enables the given domain predicates for the given type name.
     *
     * @param typeName The type name of a domain type for which to enable the given predicates
     * @param predicates The domain predicates to enable
     * @return this for chaining
     */
    public DomainBuilder withPredicate(String typeName, DomainPredicate... predicates);

    /**
     * Returns the enabled operators for the type with the given name.
     *
     * @param typeName The type name
     * @return The enabled operators
     * @since 1.0.4
     */
    public Set<DomainOperator> getEnabledOperators(String typeName);

    /**
     * Returns the enabled predicates for the type with the given name.
     *
     * @param typeName The type name
     * @return The enabled predicates
     * @since 1.0.4
     */
    public Set<DomainPredicate> getEnabledPredicates(String typeName);

    /**
     * Creates a builder for a domain function with the given name.
     *
     * @param name The function name
     * @return the domain function builder
     */
    public DomainFunctionBuilder createFunction(String name);

    /**
     * Creates a builder for a domain function that extends the domain function with the given name.
     *
     * @param name The function name
     * @return the domain function builder
     */
    public DomainFunctionBuilder extendFunction(String name);

    /**
     * Extends the domain function with the given name with the given metadata definitions, if it exists.
     *
     * @param name The function name
     * @param metadataDefinitions The metadata definitions for the domain function
     * @return this for chaining
     */
    public DomainBuilder extendFunction(String name, MetadataDefinition<?>... metadataDefinitions);

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
     * Creates an basic domain type with the given type name that extends the basic type with the given type name, if it exists.
     *
     * @param name The type name
     * @param metadataDefinitions The metadata definitions to extend the basic type with
     * @return this for chaining
     */
    public DomainBuilder extendBasicType(String name, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Creates a basic domain type with the given type name and Java type that extends the basic type with the given type name, if it exists.
     *
     * @param name The type name
     * @param javaType The Java type
     * @param metadataDefinitions The metadata definitions to extend the basic type with
     * @return this for chaining
     */
    public DomainBuilder extendBasicType(String name, Class<?> javaType, MetadataDefinition<?>... metadataDefinitions);

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
     * Creates an entity domain type with the given type name that extends the entity type with the given type name.
     *
     * @param name The type name
     * @param baseEntityType The base entity domain type definition
     * @return the entity domain builder
     */
    public EntityDomainTypeBuilder extendEntityType(String name, EntityDomainTypeDefinition baseEntityType);

    /**
     * Creates an entity domain type with the given type name and Java type.
     *
     * @param name The type name
     * @param javaType The Java type
     * @param baseEntityType The base entity domain type definition
     * @return the entity domain builder
     */
    public EntityDomainTypeBuilder extendEntityType(String name, Class<?> javaType, EntityDomainTypeDefinition baseEntityType);

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
    public EnumDomainTypeBuilder createEnumType(String name, Class<?> javaType);

    /**
     * Creates an enum domain type with the given type name that extends the enum type with the given type name.
     *
     * @param name The type name
     * @param baseEnumType The base enum domain type definition
     * @return the enum domain builder
     * @since 2.0.0
     */
    public EnumDomainTypeBuilder extendEnumType(String name, EnumDomainTypeDefinition baseEnumType);

    /**
     * Creates an enum domain type with the given type name and Java type.
     *
     * @param name The type name
     * @param javaType The Java type
     * @param baseEnumType The base enum domain type definition
     * @return the enum domain builder
     * @since 2.0.0
     */
    public EnumDomainTypeBuilder extendEnumType(String name, Class<?> javaType, EnumDomainTypeDefinition baseEnumType);

    /**
     * Sets whether function names are case sensitive.
     *
     * @param caseSensitive Whether function names are case sensitive
     * @return this for chaining
     */
    public DomainBuilder setFunctionCaseSensitive(boolean caseSensitive);

    /**
     * Returns the domain type definition with the given type name or <code>null</code>.
     *
     * @param name The type name of the desired domain type definition
     * @return the domain type definition or <code>null</code>
     */
    public DomainTypeDefinition getType(String name);

    /**
     * Returns the entity domain type definition with the given type name or <code>null</code>.
     *
     * @param name The type name of the desired entity domain type definition
     * @return the entity domain type definition or <code>null</code>
     */
    public EntityDomainTypeDefinition getEntityType(String name);

    /**
     * Returns the type definitions of the domain builder as map indexed by their type name.
     *
     * @return the type definitions of the domain builder
     */
    public Map<String, DomainTypeDefinition> getTypes();

    /**
     * Removes and returns the domain type definition with the given name or <code>null</code>.
     *
     * @param name The name of the desired domain type definition
     * @return the domain type definition or <code>null</code>
     */
    public DomainTypeDefinition removeType(String name);

    /**
     * Returns the domain function definition with the given name or <code>null</code>.
     *
     * @param name The name of the desired domain function definition
     * @return the domain function definition or <code>null</code>
     */
    public DomainFunctionDefinition getFunction(String name);

    /**
     * Returns the function definitions of the domain builder as map indexed by their function name.
     *
     * @return the function definitions of the domain builder
     */
    public Map<String, DomainFunctionDefinition> getFunctions();

    /**
     * Removes and returns the domain function definition with the given name or <code>null</code>.
     *
     * @param name The name of the desired domain function definition
     * @return the domain function definition or <code>null</code>
     */
    public DomainFunctionDefinition removeFunction(String name);

    /**
     * Returns all properties.
     *
     * @return All properties
     * @since 1.0.6
     */
    public Map<String, Object> getProperties();

    /**
     * Returns a property value by name.
     *
     * @param propertyName The name of the property
     * @return The value currently associated with that property name; may be null.
     * @since 1.0.6
     */
    public Object getProperty(String propertyName);

    /**
     * Sets a property value by name.
     *
     * @param propertyName The name of the property
     * @param propertyValue the property value
     * @since 2.0.0
     */
    public void setProperty(String propertyName, Object propertyValue);

    /**
     * Returns the registered services.
     *
     * @return the registered services
     * @since 2.0.0
     */
    Map<Class<?>, Object> getRegisteredServices();

    /**
     * Returns the registered service for the given type.
     *
     * @param serviceClass The service class
     * @param <T> The service type
     * @return the registered service
     * @since 2.0.0
     */
    <T> T getRegisteredService(Class<T> serviceClass);

    /**
     * Registers the given service for the given type.
     *
     * @param serviceClass The service class
     * @param service The service
     * @param <T> The service type
     * @return this for chaining
     * @since 2.0.0
     */
    <T> DomainBuilder withService(Class<T> serviceClass, T service);

    /**
     * Registers the given service provider.
     *
     * @param serviceProvider A custom service provider that is queried if a service was not explicitly registered
     * @return this for chaining
     * @since 2.0.0
     */
    DomainBuilder withServiceProvider(ServiceProvider serviceProvider);

    /**
     * Builds and validates the domain model as defined via this builder.
     *
     * @return The domain model
     */
    public DomainModel build();
}
