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

import com.blazebit.domain.boot.model.DomainFunctionDefinition;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.impl.boot.model.DomainFunctionArgumentDefinitionImpl;
import com.blazebit.domain.impl.boot.model.DomainFunctionArgumentDefinitionImplementor;
import com.blazebit.domain.impl.boot.model.DomainFunctionDefinitionImplementor;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;
import com.blazebit.domain.runtime.model.DomainFunctionVolatility;
import com.blazebit.domain.runtime.model.DomainType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainFunctionImpl extends AbstractMetadataHolder implements DomainFunction, DomainFunctionDefinition {

    private final String name;
    private final DomainFunctionVolatility volatility;
    private final int minArgumentCount;
    private final int argumentCount;
    private final DomainTypeImplementor resultType;
    private final List<DomainFunctionArgumentImpl> argumentList;
    private final Map<String, DomainFunctionArgumentImpl> argumentMap;
    private final Map<Class<?>, Object> metadata;

    @SuppressWarnings("unchecked")
    public DomainFunctionImpl(DomainFunctionDefinitionImplementor functionDefinition, MetamodelBuildingContext context) {
        this.name = functionDefinition.getName();
        this.volatility = functionDefinition.getVolatility();
        this.minArgumentCount = functionDefinition.getMinArgumentCount();
        this.argumentCount = functionDefinition.getArgumentCount();
        this.resultType = context.getType(functionDefinition.getResultTypeDefinition());
        List<DomainFunctionArgumentDefinitionImplementor> argumentTypeDefinitions = functionDefinition.getArguments();
        int size = Math.max(argumentTypeDefinitions.size(), argumentCount);
        int argumentDefinitionSize = argumentTypeDefinitions.size();
        List<DomainFunctionArgumentImpl> domainFunctionArguments = new ArrayList<>(size);
        Map<String, DomainFunctionArgumentImpl> domainFunctionArgumentMap = new LinkedHashMap<>(size);
        for (int i = 0; i < size; i++) {
            if (i < argumentDefinitionSize) {
                DomainFunctionArgumentImpl functionArgument = argumentTypeDefinitions.get(i).createFunctionArgument(this, context);
                domainFunctionArguments.add(functionArgument);
                if (functionArgument.getName() != null) {
                    domainFunctionArgumentMap.put(functionArgument.getName(), functionArgument);
                }
            } else {
                DomainFunctionArgumentDefinitionImpl argumentDefinition = new DomainFunctionArgumentDefinitionImpl(functionDefinition, null, i, null, false);
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
    public DomainFunctionVolatility getVolatility() {
        return volatility;
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
    public List<DomainFunctionArgumentImpl> getArguments() {
        return argumentList;
    }

    @Override
    public List<DomainFunctionArgumentImpl> getArgumentDefinitions() {
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
    public String getResultTypeName() {
        return resultType.getName();
    }

    @Override
    public boolean isResultCollection() {
        return resultType.getKind() == DomainType.DomainTypeKind.COLLECTION;
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
        StringBuilder signature = new StringBuilder(name);
        signature.append(" (");
        if (argumentList.isEmpty()) {
            if (minArgumentCount != 0) {
                for (int i = 0; i < minArgumentCount; i++) {
                    signature.append("argument").append(i + 1).append(", ");
                }
                if (argumentCount < minArgumentCount) {
                    signature.append("...");
                } else {
                    signature.setLength(signature.length() - 2);
                }
            } else if (argumentCount > 0) {
                for (int i = 0; i < minArgumentCount; i++) {
                    signature.append("argument").append(i + 1).append(", ");
                }
                signature.setLength(signature.length() - 2);
            }
        } else {
            for (int i = 0; i < argumentList.size(); i++) {
                DomainFunctionArgument argument = argumentList.get(i);
                if (argument.getName() == null) {
                    signature.append("argument").append(i + 1).append(", ");
                } else {
                    signature.append(argument.getName()).append(", ");
                }
            }
            signature.setLength(signature.length() - 2);
        }
        signature.append(')');
        return signature.toString();
    }
}
