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

import java.util.Map;

/**
 * An domain entity type builder.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface EntityDomainTypeBuilder {

    /**
     * The name of the domain type.
     *
     * @return the name
     */
    public String getName();

    /**
     * The Java type of the domain type, or <code>null</code> if none available.
     *
     * @return the Java type or <code>null</code>
     */
    public Class<?> getJavaType();

    /**
     * Sets whether attribute names are case sensitive.
     *
     * @param caseSensitive Whether attribute names are case sensitive
     * @return this for chaining
     */
    public EntityDomainTypeBuilder setCaseSensitive(boolean caseSensitive);

    /**
     * The attribute of the entity domain type with the given name or <code>null</code>.
     *
     * @param attributeName The name of the attribute
     * @return the attribute of the entity domain type
     */
    public EntityDomainTypeAttributeDefinition getAttribute(String attributeName);

    /**
     * The attributes of the entity domain type.
     *
     * @return the attributes of the entity domain type
     */
    public Map<String, EntityDomainTypeAttributeDefinition> getAttributes();

    /**
     * Adds an attribute with the given name and type name.
     *
     * @param attributeName The attribute name
     * @param typeName The type name
     * @return this for chaining
     */
    public EntityDomainTypeBuilder addAttribute(String attributeName, String typeName);

    /**
     * Adds an attribute with the given name and type name as well as metadata definitions.
     *
     * @param attributeName The attribute name
     * @param typeName The type name
     * @param metadataDefinitions The metadata for the attribute
     * @return this for chaining
     */
    public EntityDomainTypeBuilder addAttribute(String attributeName, String typeName, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds a collection attribute with the given name and a generic element type as well as metadata definitions.
     *
     * @param attributeName The attribute name
     * @param metadataDefinitions The metadata for the attribute
     * @return this for chaining
     */
    public EntityDomainTypeBuilder addCollectionAttribute(String attributeName, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds a collection attribute with the given name and element type name.
     *
     * @param attributeName The attribute name
     * @param elementTypeName The element type name
     * @return this for chaining
     */
    public EntityDomainTypeBuilder addCollectionAttribute(String attributeName, String elementTypeName);

    /**
     * Adds a collection attribute with the given name and element type name as well as metadata definitions.
     *
     * @param attributeName The attribute name
     * @param elementTypeName The element type name
     * @param metadataDefinitions The metadata for the attribute
     * @return this for chaining
     */
    public EntityDomainTypeBuilder addCollectionAttribute(String attributeName, String elementTypeName, MetadataDefinition<?>... metadataDefinitions);

    /**
     * Adds the given metadata definition to the entity type.
     *
     * @param metadataDefinition The metadata definition
     * @return this for chaining
     */
    public EntityDomainTypeBuilder withMetadata(MetadataDefinition<?> metadataDefinition);

    /**
     * Returns the metadata definitions of this domain element.
     *
     * @return the metadata definitions
     */
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions();

    /**
     * Builds and adds the domain entity type to the domain builder.
     *
     * @return the domain builder for chaining
     */
    public DomainBuilder build();

}
