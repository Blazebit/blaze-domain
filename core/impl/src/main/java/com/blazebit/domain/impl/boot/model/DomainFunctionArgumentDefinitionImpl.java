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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.DomainFunctionArgumentDefinition;
import com.blazebit.domain.boot.model.DomainFunctionDefinition;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.DomainFunctionArgumentImpl;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;
import com.blazebit.domain.runtime.model.DomainType;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainFunctionArgumentDefinitionImpl extends AbstractMetadataDefinitionHolder implements DomainFunctionArgumentDefinitionImplementor {

    private final DomainFunctionDefinition owner;
    private final String name;
    private final int index;
    private final String typeName;
    private final boolean collection;
    private DomainTypeDefinition typeDefinition;
    private DomainFunctionArgumentImpl domainFunctionArgument;

    public DomainFunctionArgumentDefinitionImpl(DomainFunctionDefinition owner, String name, int index, String typeName, boolean collection) {
        this.owner = owner;
        this.name = name;
        this.index = index;
        this.typeName = typeName;
        this.collection = collection;
    }

    public DomainFunctionArgumentDefinitionImpl(DomainFunctionDefinitionImpl owner, DomainFunctionArgumentDefinition argument) {
        super(argument);
        this.owner = owner;
        this.name = argument.getName();
        this.index = argument.getIndex();
        this.typeName = argument.getTypeName();
        this.collection = argument.isCollection();
    }

    public DomainFunctionArgumentDefinitionImpl(DomainFunctionDefinitionImpl owner, DomainFunctionArgument argument) {
        super(argument);
        this.owner = owner;
        this.name = argument.getName();
        this.index = argument.getPosition();

        if (argument.getType().getKind() == DomainType.DomainTypeKind.COLLECTION) {
            CollectionDomainType resultType = (CollectionDomainType) argument.getType();
            this.typeName = resultType.getElementType().getName();
            this.collection = true;
        } else {
            this.typeName = argument.getType().getName();
            this.collection = false;
        }
    }

    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
        if (typeName == null) {
            typeDefinition = collection ? domainBuilder.getCollectionDomainTypeDefinition(null) : null;
        } else {
            if (collection) {
                typeDefinition = domainBuilder.getDomainTypeDefinitionForFunctionArgument("Collection[" + typeName + "]");
            } else {
                typeDefinition = domainBuilder.getDomainTypeDefinitionForFunctionArgument(typeName);
            }
            if (typeDefinition == null) {
                String name = this.name == null || this.name.isEmpty() ? "" : "(" + this.name + ")";
                context.addError("The argument type '" + typeName + "' defined for the function argument index " + index + name + " of function " + owner.getName() + " is unknown!");
            }
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    @Override
    public DomainTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    @Override
    public DomainFunctionArgument getDomainFunctionArgument() {
        return domainFunctionArgument;
    }

    @Override
    public DomainFunctionArgumentImpl createFunctionArgument(DomainFunction function, MetamodelBuildingContext context) {
        if (domainFunctionArgument == null) {
            domainFunctionArgument = new DomainFunctionArgumentImpl(function, this, context);
        }

        return domainFunctionArgument;
    }
}
