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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.BasicDomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.BasicDomainTypeImpl;
import com.blazebit.domain.runtime.model.BasicDomainType;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class BasicDomainTypeDefinitionImpl extends AbstractMetadataDefinitionHolder implements BasicDomainTypeDefinition, DomainTypeDefinitionImplementor {

    private final String name;
    private final Class<?> javaType;
    private BasicDomainTypeImpl domainType;

    public BasicDomainTypeDefinitionImpl(String name, Class<?> javaType) {
        this.name = name;
        this.javaType = javaType;
    }

    public BasicDomainTypeDefinitionImpl(BasicDomainTypeDefinition basicDomainType) {
        this(basicDomainType, basicDomainType.getJavaType());
    }

    public BasicDomainTypeDefinitionImpl(BasicDomainTypeDefinition basicDomainType, Class<?> javaType) {
        super(basicDomainType);
        this.name = basicDomainType.getName();
        this.javaType = javaType;
    }

    public BasicDomainTypeDefinitionImpl(BasicDomainType basicDomainType) {
        super(basicDomainType);
        this.name = basicDomainType.getName();
        this.javaType = basicDomainType.getJavaType();
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
    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
    }

    @Override
    public BasicDomainTypeImpl getType(MetamodelBuildingContext context) {
        if (domainType == null) {
            domainType = new BasicDomainTypeImpl(this, context);
        }
        return domainType;
    }
}
