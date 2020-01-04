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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.EntityDomainTypeAttributeDefinition;
import com.blazebit.domain.boot.model.EntityDomainTypeBuilder;
import com.blazebit.domain.boot.model.MetadataDefinition;

import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EntityDomainTypeBuilderImpl implements EntityDomainTypeBuilder {

    private final DomainBuilderImpl domainBuilder;
    private final EntityDomainTypeDefinitionImpl domainTypeDefinition;

    public EntityDomainTypeBuilderImpl(DomainBuilderImpl domainBuilder, String name, Class<?> javaType) {
        this.domainBuilder = domainBuilder;
        this.domainTypeDefinition = new EntityDomainTypeDefinitionImpl(name, javaType);
    }

    @Override
    public String getName() {
        return domainTypeDefinition.getName();
    }

    @Override
    public Class<?> getJavaType() {
        return domainTypeDefinition.getJavaType();
    }

    @Override
    public EntityDomainTypeBuilderImpl setCaseSensitive(boolean caseSensitive) {
        domainTypeDefinition.setCaseSensitive(caseSensitive);
        return this;
    }

    @Override
    public EntityDomainTypeAttributeDefinition getAttribute(String attributeName) {
        return domainTypeDefinition.getAttribute(attributeName);
    }

    @Override
    public Map<String, EntityDomainTypeAttributeDefinition> getAttributes() {
        return domainTypeDefinition.getAttributes();
    }

    @Override
    public EntityDomainTypeBuilderImpl addAttribute(String attributeName, String typeName) {
        domainTypeDefinition.addAttribute(new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, typeName, null, false));
        return this;
    }

    @Override
    public EntityDomainTypeBuilderImpl addAttribute(String attributeName, Class<?> javaType) {
        domainTypeDefinition.addAttribute(new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, null, javaType, false));
        return this;
    }

    @Override
    public EntityDomainTypeBuilderImpl addCollectionAttribute(String attributeName, String typeName) {
        domainTypeDefinition.addAttribute(new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, typeName, null, true));
        return this;
    }

    @Override
    public EntityDomainTypeBuilderImpl addCollectionAttribute(String attributeName, Class<?> elementJavaType) {
        domainTypeDefinition.addAttribute(new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, null, elementJavaType, true));
        return this;
    }

    @Override
    public EntityDomainTypeBuilder addAttribute(String attributeName, String elementTypeName, MetadataDefinition<?>... metadataDefinitions) {
        EntityDomainTypeAttributeDefinitionImpl attributeDefinition = new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, elementTypeName, null, false);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            attributeDefinition.withMetadataDefinition(metadataDefinition);
        }

        domainTypeDefinition.addAttribute(attributeDefinition);
        return this;
    }

    @Override
    public EntityDomainTypeBuilder addAttribute(String attributeName, Class<?> javaType, MetadataDefinition<?>... metadataDefinitions) {
        EntityDomainTypeAttributeDefinitionImpl attributeDefinition = new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, null, javaType, false);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            attributeDefinition.withMetadataDefinition(metadataDefinition);
        }

        domainTypeDefinition.addAttribute(attributeDefinition);
        return this;
    }

    @Override
    public EntityDomainTypeBuilder addCollectionAttribute(String attributeName, String elementTypeName, MetadataDefinition<?>... metadataDefinitions) {
        EntityDomainTypeAttributeDefinitionImpl attributeDefinition = new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, elementTypeName, null, true);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            attributeDefinition.withMetadataDefinition(metadataDefinition);
        }

        domainTypeDefinition.addAttribute(attributeDefinition);
        return this;
    }

    @Override
    public EntityDomainTypeBuilder addCollectionAttribute(String attributeName, Class<?> elementJavaType, MetadataDefinition<?>... metadataDefinitions) {
        EntityDomainTypeAttributeDefinitionImpl attributeDefinition = new EntityDomainTypeAttributeDefinitionImpl(domainTypeDefinition, attributeName, null, elementJavaType, true);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            attributeDefinition.withMetadataDefinition(metadataDefinition);
        }

        domainTypeDefinition.addAttribute(attributeDefinition);
        return this;
    }

    @Override
    public EntityDomainTypeBuilder withMetadata(MetadataDefinition<?> metadataDefinition) {
        domainTypeDefinition.withMetadataDefinition(metadataDefinition);
        return this;
    }

    @Override
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions() {
        return domainTypeDefinition.getMetadataDefinitions();
    }

    @Override
    public DomainBuilderImpl build() {
        return domainBuilder.withDomainTypeDefinition(domainTypeDefinition);
    }
}
