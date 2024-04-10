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

package com.blazebit.domain.impl.runtime.model;

import com.blazebit.domain.boot.model.BasicDomainTypeDefinition;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.UnionDomainTypeDefinition;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.UnionDomainType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 2.0.6
 */
public class UnionDomainTypeImpl extends AbstractDomainType implements UnionDomainType, BasicDomainTypeDefinition {

    private final List<DomainType> unionElementTypes;

    public UnionDomainTypeImpl(UnionDomainTypeDefinition typeDefinition, MetamodelBuildingContext context) {
        super(typeDefinition, context);
        List<DomainTypeDefinition> unionElements = typeDefinition.getUnionElements();
        List<DomainType> unionElementTypes = new ArrayList<>(unionElements.size());
        for (DomainTypeDefinition unionElement : unionElements) {
            unionElementTypes.add(context.getType(unionElement));
        }
        this.unionElementTypes = Collections.unmodifiableList(unionElementTypes);
    }

    @Override
    public DomainTypeKind getKind() {
        return DomainTypeKind.UNION;
    }

    @Override
    public <T> T getMetadata(Class<T> metadataType) {
        return null;
    }

    @Override
    public Map<Class<?>, Object> getMetadata() {
        return Collections.emptyMap();
    }

    @Override
    public List<DomainType> getUnionElements() {
        return unionElementTypes;
    }

    @Override
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions() {
        return Collections.emptyMap();
    }

    @Override
    public String toString() {
        return getName();
    }
}
