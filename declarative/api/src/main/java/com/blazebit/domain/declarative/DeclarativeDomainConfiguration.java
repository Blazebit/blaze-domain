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

package com.blazebit.domain.declarative;

import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.declarative.spi.DeclarativeAttributeMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionParameterMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeMetadataProcessor;
import com.blazebit.domain.declarative.spi.ServiceProvider;
import com.blazebit.domain.declarative.spi.TypeResolver;
import com.blazebit.domain.declarative.spi.TypeResolverDecorator;
import com.blazebit.domain.runtime.model.DomainModel;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DeclarativeDomainConfiguration extends ServiceProvider<DeclarativeDomainConfiguration> {

    /**
     * Adds the given domain type java class for later analysis to add it as domain type to the domain builder.
     *
     * @param domainTypeClass The domain type java class to analyze
     * @return this for chaining
     */
    DeclarativeDomainConfiguration addDomainType(Class<?> domainTypeClass);

    /**
     * Adds the given domain type java class for later analysis to add it as domain type to the domain builder.
     *
     * @param domainTypeClass The domain type java class to analyze
     * @param domainTypeAnnotation The domain type annotation to use
     * @return this for chaining
     */
    DeclarativeDomainConfiguration addDomainType(Class<?> domainTypeClass, DomainType domainTypeAnnotation);

    /**
     * Adds the given domain functions java class for later analysis to add its functions as domain function to the domain builder.
     *
     * @param domainFunctionsClass The domain functions java class to analyze
     * @return this for chaining
     */
    DeclarativeDomainConfiguration addDomainFunctions(Class<?> domainFunctionsClass);

    /**
     * Adds the given domain functions java class for later analysis to add its functions as domain function to the domain builder.
     *
     * @param domainFunctionsClass The domain functions java class to analyze
     * @param domainFunctionsAnnotation The domain functions annotation to use
     * @return this for chaining
     */
    DeclarativeDomainConfiguration addDomainFunctions(Class<?> domainFunctionsClass, DomainFunctions domainFunctionsAnnotation);

    /**
     * Returns the type resolver.
     *
     * @return the type resolver
     */
    TypeResolver getTypeResolver();

    /**
     * Sets the type resolver.
     *
     * @param typeResolver The type resolver
     * @return this for chaining
     */
    DeclarativeDomainConfiguration setTypeResolver(TypeResolver typeResolver);

    /**
     * Returns the type resolver decorators.
     *
     * @return the type resolver decorators
     */
    List<TypeResolverDecorator> getTypeResolverDecorators();

    /**
     * Registers the given type resolver decorator.
     *
     * @param typeResolverDecorator The type resolver decorator
     * @return this for chaining
     */
    DeclarativeDomainConfiguration withTypeResolverDecorator(TypeResolverDecorator typeResolverDecorator);

    /**
     * Registers the given declarative metadata processor to for analyzing metadata of domain types.
     *
     * @param metadataProcessor The declarative metadata processor
     * @return this for chaining
     */
    DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeMetadataProcessor<? extends Annotation> metadataProcessor);

    /**
     * Registers the given declarative attribute metadata processor to for analyzing metadata of entity attributes.
     *
     * @param metadataProcessor The declarative attribute metadata processor
     * @return this for chaining
     */
    DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeAttributeMetadataProcessor<? extends Annotation> metadataProcessor);

    /**
     * Registers the given declarative function metadata processor to for analyzing metadata of domain functions.
     *
     * @param metadataProcessor The declarative function metadata processor
     * @return this for chaining
     */
    DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeFunctionMetadataProcessor<? extends Annotation> metadataProcessor);

    /**
     * Registers the given declarative function parameter metadata processor to for analyzing metadata of domain function parameters.
     *
     * @param metadataProcessor The declarative function parameter metadata processor
     * @return this for chaining
     */
    DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeFunctionParameterMetadataProcessor<? extends Annotation> metadataProcessor);

    /**
     * Sets whether function names are case sensitive.
     *
     * @param caseSensitive Whether function names are case sensitive
     * @return this for chaining
     */
    DeclarativeDomainConfiguration setFunctionCaseSensitive(boolean caseSensitive);

    /**
     * Returns all properties.
     *
     * @return All properties
     * @since 1.0.6
     */
    Map<String, Object> getProperties();

    /**
     * Returns a property value by name.
     *
     * @param propertyName The name of the property
     * @return The value currently associated with that property name; may be null.
     * @since 1.0.6
     */
    Object getProperty(String propertyName);

    /**
     * Builds and validates the domain model as defined via this builder.
     *
     * @return The domain model
     */
    DomainModel createDomainModel();

    /**
     * Builds and validates the domain model as defined via this builder.
     *
     * @param domainBuilder The domain builder on which to apply the declarative configuration
     * @return The domain model
     * @since 1.0.6
     */
    DomainModel createDomainModel(DomainBuilder domainBuilder);
}
