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

import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.runtime.model.DomainType;

import java.util.*;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class MetamodelBuildingContext {

    private final DomainBuilderImpl domainBuilder;
    private final Map<DomainTypeDefinition<?>, DomainType> buildingTypes = new HashMap<>();
    private List<String> errors = new ArrayList<>();

    public MetamodelBuildingContext(DomainBuilderImpl domainBuilder) {
        this.domainBuilder = domainBuilder;
        DomainModel baseModel = domainBuilder.getBaseModel();
        if (baseModel != null) {
            for (DomainType domainType : baseModel.getTypes().values()) {
                buildingTypes.put((DomainTypeDefinition<?>) domainType, domainType);
            }
        }
    }

    public void addError(String error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addType(DomainTypeDefinition<?> typeDefinition, DomainType domainType) {
        buildingTypes.put(typeDefinition, domainType);
    }

    public DomainType getType(DomainTypeDefinition<?> typeDefinition) {
        DomainType domainType = buildingTypes.get(typeDefinition);
        if (domainType == null && typeDefinition != null) {
            domainType = ((DomainTypeDefinitionImplementor<?>) typeDefinition).getType(this);
        }

        return domainType;
    }

    public Set<DomainOperator> getOperators(DomainTypeDefinition<?> typeDefinition) {
        return domainBuilder.getOperators(typeDefinition);
    }

    public Set<DomainPredicate> getPredicates(DomainTypeDefinition<?> typeDefinition) {
        return domainBuilder.getPredicates(typeDefinition);
    }

    public Map<Class<?>, Object> createMetadata(MetadataDefinitionHolder definitionHolder) {
        Map<Class<?>, MetadataDefinition<?>> metadataDefinitions = definitionHolder.getMetadataDefinitions();
        Map<Class<?>, Object> metadata = new HashMap<>(metadataDefinitions.size());
        for (Map.Entry<Class<?>, MetadataDefinition<?>> entry : metadataDefinitions.entrySet()) {
            metadata.put(entry.getKey(), entry.getValue().build(definitionHolder));
        }

        return metadata;
    }
}
