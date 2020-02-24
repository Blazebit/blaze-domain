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

package com.blazebit.domain.impl.runtime.model;

import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.BasicDomainType;

import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BasicDomainTypeImpl extends AbstractDomainTypeImpl implements BasicDomainType {

    private final Map<Class<?>, Object> metadata;

    public BasicDomainTypeImpl(DomainTypeDefinition<?> typeDefinition, MetamodelBuildingContext context) {
        super(typeDefinition, context);
        this.metadata = context.createMetadata(typeDefinition);
    }

    @Override
    public DomainTypeKind getKind() {
        return DomainTypeKind.BASIC;
    }

    @Override
    public <T> T getMetadata(Class<T> metadataType) {
        return (T) metadata.get(metadataType);
    }

    @Override
    public Map<Class<?>, Object> getMetadata() {
        return metadata;
    }

    @Override
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions() {
        return getMetadataDefinitions(metadata);
    }

    @Override
    public String toString() {
        return getName();
    }
}
