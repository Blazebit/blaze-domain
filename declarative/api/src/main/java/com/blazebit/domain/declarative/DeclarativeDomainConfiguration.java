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

package com.blazebit.domain.declarative;

import com.blazebit.domain.declarative.spi.DeclarativeAttributeMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionParameterMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeMetadataProcessor;
import com.blazebit.domain.declarative.spi.TypeResolver;
import com.blazebit.domain.runtime.model.DomainModel;

import java.lang.annotation.Annotation;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DeclarativeDomainConfiguration {

    /**
     * Analyzes the given domain type java class and adds it as domain type to the domain builder.
     *
     * @param domainTypeClass The domain type java class to analyze
     * @return this for chaining
     */
    DeclarativeDomainConfiguration addDomainType(Class<?> domainTypeClass);

    /**
     * Analyzes the given domain functions java class and adds its functions as domain function to the domain builder.
     *
     * @param domainFunctionsClass The domain functions java class to analyze
     * @return this for chaining
     */
    DeclarativeDomainConfiguration addDomainFunctions(Class<?> domainFunctionsClass);

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
     * Builds and validates the domain model as defined via this builder.
     *
     * @return The domain model
     */
    DomainModel createDomainModel();
}
