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

package com.blazebit.domain.impl.runtime.model;

import com.blazebit.domain.impl.boot.model.DomainFunctionArgumentDefinitionImpl;
import com.blazebit.domain.impl.boot.model.DomainFunctionArgumentDefinitionImplementor;
import com.blazebit.domain.impl.boot.model.DomainFunctionDefinitionImplementor;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;
import com.blazebit.domain.runtime.model.DomainType;

import java.util.*;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainFunctionImpl implements DomainFunction {

    private final String name;
    private final int minArgumentCount;
    private final int argumentCount;
    private final DomainType resultType;
    private final List<DomainFunctionArgument> argumentList;
    private final Map<String, DomainFunctionArgument> argumentMap;
    private final Map<Class<?>, Object> metadata;

    @SuppressWarnings("unchecked")
    public DomainFunctionImpl(DomainFunctionDefinitionImplementor functionDefinition, MetamodelBuildingContext context) {
        this.name = functionDefinition.getName();
        this.minArgumentCount = functionDefinition.getMinArgumentCount();
        this.argumentCount = functionDefinition.getArgumentCount();
        this.resultType = context.getType(functionDefinition.getResultTypeDefinition());
        List<DomainFunctionArgumentDefinitionImplementor> argumentTypeDefinitions = functionDefinition.getArguments();
        int size = Math.max(argumentTypeDefinitions.size(), argumentCount);
        int argumentDefinitionSize = argumentTypeDefinitions.size();
        List<DomainFunctionArgument> domainFunctionArguments = new ArrayList<>(size);
        Map<String, DomainFunctionArgument> domainFunctionArgumentMap = new LinkedHashMap<>(size);
        for (int i = 0; i < size; i++) {
            if (i < argumentDefinitionSize) {
                DomainFunctionArgument functionArgument = argumentTypeDefinitions.get(i).createFunctionArgument(this, context);
                domainFunctionArguments.add(functionArgument);
                if (functionArgument.getName() != null) {
                    domainFunctionArgumentMap.put(functionArgument.getName(), functionArgument);
                }
            } else {
                DomainFunctionArgumentDefinitionImpl argumentDefinition = new DomainFunctionArgumentDefinitionImpl(functionDefinition, null, i, null, null, false);
                domainFunctionArguments.add(argumentDefinition.createFunctionArgument(this, context));
            }
        }
        this.argumentList = Collections.unmodifiableList(domainFunctionArguments);
        this.argumentMap = Collections.unmodifiableMap(domainFunctionArgumentMap);
        this.metadata = context.createMetadata(functionDefinition);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMinArgumentCount() {
        return minArgumentCount;
    }

    @Override
    public int getArgumentCount() {
        return argumentCount;
    }

    @Override
    public List<DomainFunctionArgument> getArguments() {
        return argumentList;
    }

    @Override
    public DomainFunctionArgument getArgument(String argumentName) {
        return argumentMap.get(argumentName);
    }

    @Override
    public DomainFunctionArgument getArgument(int argumentIndex) {
        return argumentList.get(argumentIndex);
    }

    @Override
    public DomainType getResultType() {
        return resultType;
    }

    @Override
    public <T> T getMetadata(Class<T> metadataType) {
        return (T) metadata.get(metadataType);
    }
}
