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

import com.blazebit.domain.boot.model.CollectionDomainTypeDefinition;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.CollectionDomainTypeImpl;
import com.blazebit.domain.runtime.model.CollectionDomainType;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class CollectionDomainTypeDefinitionImpl extends AbstractMetadataDefinitionHolder implements CollectionDomainTypeDefinition, DomainTypeDefinitionImplementor {

    private final String name;
    private final Class<?> javaType;
    private final String elementTypeName;
    private DomainTypeDefinition elementTypeDefinition;
    private CollectionDomainTypeImpl domainType;

    public CollectionDomainTypeDefinitionImpl(String name, Class<?> javaType, DomainTypeDefinition elementTypeDefinition) {
        this.name = name;
        this.javaType = javaType;
        this.elementTypeName = elementTypeDefinition == null ? null : elementTypeDefinition.getName();
        this.elementTypeDefinition = elementTypeDefinition;
    }

    public CollectionDomainTypeDefinitionImpl(CollectionDomainType collectionDomainType) {
        super(collectionDomainType);
        this.name = collectionDomainType.getName();
        this.javaType = collectionDomainType.getJavaType();
        this.elementTypeName = collectionDomainType.getElementType() == null ? null : collectionDomainType.getElementType().getName();
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
    public DomainTypeDefinition getElementType() {
        return elementTypeDefinition;
    }

    @Override
    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
        this.domainType = null;
        if (elementTypeName == null) {
            elementTypeDefinition = null;
            return;
        } else {
            elementTypeDefinition = domainBuilder.getDomainTypeDefinition(elementTypeName);
        }
        if (elementTypeDefinition == null) {
            context.addError("The element type '" + elementTypeName + "' defined for the collection type " + name + " is unknown!");
        }
    }

    @Override
    public CollectionDomainTypeImpl getType(MetamodelBuildingContext context) {
        if (domainType == null) {
            domainType = new CollectionDomainTypeImpl(this, context);
        }
        return domainType;
    }
}
