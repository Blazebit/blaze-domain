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
import com.blazebit.domain.spi.ServiceProvider;

import java.util.List;
import java.util.Map;

/**
 * A type checked domain model that can be used for domain introspection.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainModel extends ServiceProvider {

    /**
     * Return the parent domain model or <code>null</code>.
     *
     * @return the parent domain model or <code>null</code>
     * @since 2.0.0
     */
    public DomainModel getParentDomainModel();

    /**
     * Returns the domain type with the given type name or <code>null</code>.
     *
     * @param name The type name of the desired domain type
     * @return the domain type or <code>null</code>
     */
    public DomainType getType(String name);

    /**
     * Returns the entity domain type with the given type name or <code>null</code>.
     *
     * @param name The type name of the desired domain type
     * @return the entity domain type or <code>null</code>
     */
    public EntityDomainType getEntityType(String name);

    /**
     * Returns the enum domain type with the given type name or <code>null</code>.
     *
     * @param name The type name of the desired domain type
     * @return the enum domain type or <code>null</code>
     * @since 2.0.3
     */
    public EnumDomainType getEnumType(String name);

    /**
     * Returns the collection domain type with the given element domain type name or <code>null</code>.
     *
     * @param elementDomainTypeName The element domain type name of the desired collection domain type
     * @return the collection domain type or <code>null</code>
     */
    public CollectionDomainType getCollectionType(String elementDomainTypeName);

    /**
     * Returns the types of the domain model as map indexed by their type name.
     * Note that some entries can have a null value which marks them as explicitly removed.
     *
     * @return the types of the domain model
     */
    public Map<String, DomainType> getTypes();

    /**
     * Returns the domain function with the given name or <code>null</code>.
     *
     * @param name The name of the desired domain function
     * @return the domain function or <code>null</code>
     */
    public DomainFunction getFunction(String name);

    /**
     * Returns the functions of the domain model as map indexed by their function name.
     * Note that some entries can have a null value which marks them as explicitly removed.
     *
     * @return the functions of the domain model
     */
    public Map<String, DomainFunction> getFunctions();

    /**
     * Returns the function type resolver for the function with the given name.
     *
     * @param functionName The name of the function
     * @return the function type resolver
     */
    public DomainFunctionTypeResolver getFunctionTypeResolver(String functionName);

    /**
     * Returns the function type resolvers of the domain model as map indexed by their function name.
     * Note that some entries can have a null value which marks them as explicitly removed.
     *
     * @return the function type resolvers of the domain model
     */
    public Map<String, DomainFunctionTypeResolver> getFunctionTypeResolvers();

    /**
     * Returns the operation type resolver for resolving the type of the domain operator applied to the given type name.
     *
     * @param typeName The type name for which to apply the domain operator
     * @param operator The operator to apply on the type name
     * @return the operation type resolver
     */
    public DomainOperationTypeResolver getOperationTypeResolver(String typeName, DomainOperator operator);

    /**
     * Returns the predicate type resolver for resolving the type of the domain predicate applied to the given type name.
     *
     * @param typeName The type name for which to apply the domain operator
     * @param predicateType The predicate to apply on the type name
     * @return the predicate type resolver
     */
    public DomainPredicateTypeResolver getPredicateTypeResolver(String typeName, DomainPredicate predicateType);

    /**
     * Returns the operation type resolvers of the domain model as map indexed by their type name.
     * Note that some entries can have a null value which marks them as explicitly removed.
     *
     * @return the operation type resolvers of the domain model
     */
    public Map<String, Map<DomainOperator, DomainOperationTypeResolver>> getOperationTypeResolvers();

    /**
     * Returns the predicate type resolvers of the domain model as map indexed by their type name.
     * Note that some entries can have a null value which marks them as explicitly removed.
     *
     * @return the predicate type resolvers of the domain model
     */
    public Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> getPredicateTypeResolvers();

    /**
     * The default result type for predicates.
     *
     * @return The default result type
     * @since 2.0.0
     */
    public DomainType getPredicateDefaultResultType();

    /**
     * Returns the domain serializers.
     *
     * @return the domain serializers
     */
    public List<DomainSerializer<?>> getDomainSerializers();

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
     * Serializes the domain model to the given target type with the given format.
     *
     * @param targetType The target type
     * @param format The serialization format
     * @param properties Serialization properties
     * @param <T> The target type
     * @return The serialized form
     */
    default <T> T serialize(Class<T> targetType, String format, Map<String, Object> properties) {
        return serialize(null, targetType, format, properties);
    }

    /**
     * Serializes the domain model to the given target type with the given format.
     * It only serializes elements that do not belong to the given base model already or are overridden.
     *
     * @param baseModel The base domain model
     * @param targetType The target type
     * @param format The serialization format
     * @param properties Serialization properties
     * @param <T> The target type
     * @return The serialized form
     * @since 2.0.0
     */
    public <T> T serialize(DomainModel baseModel, Class<T> targetType, String format, Map<String, Object> properties);
}
