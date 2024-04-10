/*
 * Copyright 2019 - 2024 Blazebit.
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

import java.util.Map;

/**
 * A domain enum type builder.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface EnumDomainTypeBuilder {

    /**
     * The name of the domain type.
     *
     * @return the name
     */
    public String getName();

    /**
     * The Java type of the enum domain type, or <code>null</code> if none available.
     *
     * @return the Java type or <code>null</code>
     */
    public Class<?> getJavaType();

    /**
     * The enum value of the enum domain type with the given name or <code>null</code>.
     *
     * @param name The name of the enum value
     * @return the enum value of the enum domain type
     */
    public EnumDomainTypeValueDefinition getEnumValue(String name);

    /**
     * The enum values of the enum domain type.
     *
     * @return the enum values of the enum domain type
     */
    public Map<String, EnumDomainTypeValueDefinition> getEnumValues();

    /**
     * Sets whether enum values are case sensitive.
     *
     * @param caseSensitive Whether values are case sensitive
     * @return this for chaining
     */
    public EnumDomainTypeBuilder setCaseSensitive(boolean caseSensitive);

    /**
     * Adds the given value to the enum values.
     *
     * @param value The enum value to add
     * @return this for chaining
     */
    public EnumDomainTypeBuilder withValue(String value);

    /**
     * Adds the given value to the enum values with the given metadata definitions.
     *
     * @param value The enum value to add
     * @param metadataDefinitions The metadata for the enum value
     * @return this for chaining
     */
    public EnumDomainTypeBuilder withValue(String value, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds the given metadata definition to the enum type.
     *
     * @param metadataDefinition The metadata definition
     * @return this for chaining
     */
    public EnumDomainTypeBuilder withMetadata(MetadataDefinition<?> metadataDefinition);

    /**
     * Returns the metadata definitions of this domain element.
     *
     * @return the metadata definitions
     */
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions();

    /**
     * Builds and adds the domain enum type to the domain builder.
     *
     * @return the domain builder for chaining
     */
    public DomainBuilder build();

}
