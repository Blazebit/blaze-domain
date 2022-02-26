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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.UnionDomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.UnionDomainTypeImpl;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.UnionDomainType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 2.0.6
 */
public class UnionDomainTypeDefinitionImpl extends AbstractMetadataDefinitionHolder implements UnionDomainTypeDefinition, DomainTypeDefinitionImplementor {

    private final String name;
    private final List<DomainTypeDefinition> unionElementTypeDefinitions;
    private UnionDomainTypeImpl domainType;

    public UnionDomainTypeDefinitionImpl(String name, DomainTypeDefinition[] unionElementTypeDefinitions) {
        this.name = name;
        this.unionElementTypeDefinitions = Collections.unmodifiableList(Arrays.asList(unionElementTypeDefinitions));
    }

    public UnionDomainTypeDefinitionImpl(UnionDomainType unionDomainType, DomainBuilder domainBuilder) {
        super(unionDomainType);
        this.name = unionDomainType.getName();
        List<DomainType> unionElements = unionDomainType.getUnionElements();
        List<DomainTypeDefinition> unionElementTypeDefinitions = new ArrayList<>(unionElements.size());
        for (DomainType unionElement : unionElements) {
            unionElementTypeDefinitions.add(domainBuilder.getType(unionElement.getName()));
        }
        this.unionElementTypeDefinitions = Collections.unmodifiableList(unionElementTypeDefinitions);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<?> getJavaType() {
        return null;
    }

    @Override
    public List<DomainTypeDefinition> getUnionElements() {
        return unionElementTypeDefinitions;
    }

    @Override
    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
    }

    @Override
    public UnionDomainTypeImpl getType(MetamodelBuildingContext context) {
        if (domainType == null) {
            domainType = new UnionDomainTypeImpl(this, context);
        }
        return domainType;
    }
}
