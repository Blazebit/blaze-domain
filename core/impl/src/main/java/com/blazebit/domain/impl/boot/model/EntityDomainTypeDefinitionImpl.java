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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.EntityDomainTypeAttributeDefinition;
import com.blazebit.domain.boot.model.EntityDomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.EntityDomainTypeImpl;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.EntityDomainType;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EntityDomainTypeDefinitionImpl extends MetadataDefinitionHolderImpl<EntityDomainTypeDefinition> implements EntityDomainTypeDefinition, DomainTypeDefinitionImplementor<EntityDomainTypeDefinition> {

    private final String name;
    private final Class<?> javaType;
    private final Map<String, EntityDomainTypeAttributeDefinitionImpl> attributes;
    private boolean caseSensitive = true;
    private EntityDomainType domainType;

    public EntityDomainTypeDefinitionImpl(String name, Class<?> javaType) {
        this.name = name;
        this.javaType = javaType;
        this.attributes = new HashMap<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getJavaType() {
        return javaType;
    }

    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    public void addAttribute(EntityDomainTypeAttributeDefinitionImpl attribute) {
        attributes.put(attribute.getName(), attribute);
    }

    @Override
    public EntityDomainTypeAttributeDefinition getAttribute(String name) {
        return attributes.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, EntityDomainTypeAttributeDefinition> getAttributes() {
        return (Map<String, EntityDomainTypeAttributeDefinition>) (Map<?, ?>) attributes;
    }

    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
        this.domainType = null;
        for (EntityDomainTypeAttributeDefinitionImpl attributeDefinition : attributes.values()) {
            attributeDefinition.bindTypes(domainBuilder, context);
        }
    }

    @Override
    public DomainType getType(MetamodelBuildingContext context) {
        if (domainType == null) {
            domainType = new EntityDomainTypeImpl(this, context);
        }
        return domainType;
    }
}
