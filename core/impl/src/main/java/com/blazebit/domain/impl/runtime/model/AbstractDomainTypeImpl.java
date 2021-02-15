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

package com.blazebit.domain.impl.runtime.model;

import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.runtime.model.DomainType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public abstract class AbstractDomainTypeImpl implements DomainType, DomainTypeDefinition, Serializable {

    private final String name;
    private final Class<?> javaType;
    private final Set<DomainOperator> enabledOperators;
    private final Set<DomainPredicate> enabledPredicates;

    public AbstractDomainTypeImpl(DomainTypeDefinition<?> typeDefinition, MetamodelBuildingContext context) {
        context.addType(typeDefinition, this);
        this.name = typeDefinition.getName();
        this.javaType = typeDefinition.getJavaType();
        this.enabledOperators = context.getOperators(typeDefinition);
        this.enabledPredicates = context.getPredicates(typeDefinition);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getJavaType() {
        return javaType;
    }

    @Override
    public Set<DomainOperator> getEnabledOperators() {
        return enabledOperators;
    }

    @Override
    public Set<DomainPredicate> getEnabledPredicates() {
        return enabledPredicates;
    }

    @Override
    public MetadataDefinitionHolder withMetadataDefinition(MetadataDefinition metadataDefinition) {
        throw new UnsupportedOperationException();
    }

    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions(Map<Class<?>, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return (Map<Class<?>, MetadataDefinition<?>>) (Map<?, ?>) metadata;
        }
        Map<Class<?>, MetadataDefinition<?>> map = new HashMap<>(metadata.size());
        for (Map.Entry<Class<?>, Object> entry : metadata.entrySet()) {
            map.put(entry.getKey(), new RuntimeMetadataDefinition(javaType, entry.getValue()));
        }
        return map;
    }

    @Override
    public String toString() {
        return (name == null ? "n/a" : name) + (javaType == null ? "" : "[" + javaType.getName() + "]");
    }
}
