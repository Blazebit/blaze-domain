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

package com.blazebit.domain.declarative.persistence;

import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;

/**
 * An implementation for the {@link com.blazebit.domain.persistence.EntityType} interface that also is a {@link MetadataDefinition}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
class EntityTypeImpl implements com.blazebit.domain.persistence.EntityType, MetadataDefinition<com.blazebit.domain.persistence.EntityType> {

    private final Class<?> entityClass;
    private final String entityName;

    /**
     * Create a new instance based on the given annotation.
     *
     * @param entityType the entity type annotation
     */
    public EntityTypeImpl(EntityType entityType) {
        this(entityType.value(), getEntityName(entityType.value(), entityType.entityName()));
    }

    /**
     * Creates a new instance based on the given entity class and entity name.
     *
     * @param entityClass The entity class
     * @param entityName The entity name
     */
    public EntityTypeImpl(Class<?> entityClass, String entityName) {
        this.entityClass = entityClass;
        this.entityName = entityName;
    }

    private static String getEntityName(Class<?> entityClass, String entityName) {
        if (!entityName.isEmpty()) {
            return entityName;
        }

        return entityClass.getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> getEntityClass() {
        return entityClass;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntityName() {
        return entityName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<com.blazebit.domain.persistence.EntityType> getJavaType() {
        return com.blazebit.domain.persistence.EntityType.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public com.blazebit.domain.persistence.EntityType build(MetadataDefinitionHolder<?> definitionHolder) {
        return this;
    }
}
