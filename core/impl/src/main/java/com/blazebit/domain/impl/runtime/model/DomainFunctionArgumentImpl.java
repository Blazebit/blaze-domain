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

import com.blazebit.domain.impl.boot.model.DomainFunctionArgumentDefinitionImplementor;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;
import com.blazebit.domain.runtime.model.DomainType;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainFunctionArgumentImpl implements DomainFunctionArgument, Serializable {

    private final DomainFunction owner;
    private final String name;
    private final int index;
    private final DomainType type;
    private final Map<Class<?>, Object> metadata;

    public DomainFunctionArgumentImpl(DomainFunction function, DomainFunctionArgumentDefinitionImplementor argumentDefinition, MetamodelBuildingContext context) {
        this.owner = function;
        this.name = argumentDefinition.getName();
        this.index = argumentDefinition.getIndex();
        this.type = context.getType(argumentDefinition.getTypeDefinition());
        this.metadata = context.createMetadata(argumentDefinition);
    }

    @Override
    public DomainFunction getOwner() {
        return owner;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getPosition() {
        return index;
    }

    @Override
    public DomainType getType() {
        return type;
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
    public String toString() {
        if (name == null) {
            return "DomainFunctionArgument{" +
                "index=" + index +
                ", type=" + type +
                '}';
        } else {
            return "DomainFunctionArgument{" +
                "name='" + name + '\'' +
                ", index=" + index +
                ", type=" + type +
                '}';
        }
    }
}
