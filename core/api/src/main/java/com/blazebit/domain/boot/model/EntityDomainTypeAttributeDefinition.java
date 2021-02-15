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

/**
 * A domain entity type attribute definition.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface EntityDomainTypeAttributeDefinition extends MetadataDefinitionHolder<EntityDomainTypeAttributeDefinition> {

    /**
     * The name of the entity type attribute.
     *
     * @return the name
     */
    public String getName();

    /**
     * The entity domain type definition that owns this attribute.
     *
     * @return the owner of this attribute
     */
    public EntityDomainTypeDefinition getOwner();

    /**
     * Returns whether this attribute is a collection.
     *
     * @return <code>true</code> if this attribute is a collection, <code>false</code> otherwise
     */
    public boolean isCollection();

    /**
     * Returns the type name of this attribute.
     *
     * @return the type name of this attribute
     */
    public String getTypeName();

    /**
     * Returns the java type of this attribute.
     *
     * @return the java type of this attribute
     * @deprecated The domain type index by java type is deprecated and will be removed in 2.0. Use {@link #getTypeName()} instead
     */
    @Deprecated
    public Class<?> getJavaType();
}
