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

import com.blazebit.domain.boot.model.CollectionDomainTypeDefinition;
import com.blazebit.domain.boot.model.DomainFunctionArgumentDefinition;
import com.blazebit.domain.boot.model.DomainFunctionDefinition;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.DomainFunctionImpl;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;
import com.blazebit.domain.runtime.model.DomainFunctionVolatility;
import com.blazebit.domain.runtime.model.DomainType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainFunctionDefinitionImpl extends AbstractMetadataDefinitionHolder implements DomainFunctionDefinitionImplementor {

    private final String name;
    private DomainFunctionVolatility volatility = DomainFunctionVolatility.IMMUTABLE;
    private int minArgumentCount = -1;
    private int argumentCount = -1;
    private String resultTypeName;
    private boolean resultCollection;
    private Boolean positional;
    private List<DomainFunctionArgumentDefinitionImpl> argumentDefinitions = new ArrayList<>();
    private DomainTypeDefinition resultTypeDefinition;
    private DomainFunction function;

    public DomainFunctionDefinitionImpl(String name) {
        this.name = name;
    }

    public DomainFunctionDefinitionImpl(DomainFunctionDefinition domainFunction) {
        super(domainFunction);
        this.name = domainFunction.getName();
        this.minArgumentCount = domainFunction.getMinArgumentCount();
        this.argumentCount = domainFunction.getArgumentCount();
        this.resultTypeName = domainFunction.getResultTypeName();
        this.resultCollection = domainFunction.isResultCollection();
        for (DomainFunctionArgumentDefinition argument : domainFunction.getArgumentDefinitions()) {
            addArgumentDefinition(argument);
        }
    }

    public DomainFunctionDefinitionImpl(DomainFunction domainFunction) {
        super(domainFunction);
        this.name = domainFunction.getName();
        this.minArgumentCount = domainFunction.getMinArgumentCount();
        this.argumentCount = domainFunction.getArgumentCount();
        if (domainFunction.getResultType() != null) {
            if (domainFunction.getResultType().getKind() == DomainType.DomainTypeKind.COLLECTION) {
                CollectionDomainType resultType = (CollectionDomainType) domainFunction.getResultType();
                this.resultTypeName = resultType.getElementType().getName();
                this.resultCollection = true;
            } else {
                this.resultTypeName = domainFunction.getResultType().getName();
                this.resultCollection = false;
            }
        }
        for (DomainFunctionArgument argument : domainFunction.getArguments()) {
            argumentDefinitions.add(new DomainFunctionArgumentDefinitionImpl(this, argument));
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DomainFunctionVolatility getVolatility() {
        return volatility;
    }

    public void setVolatility(DomainFunctionVolatility volatility) {
        this.volatility = volatility;
    }

    @Override
    public int getMinArgumentCount() {
        return minArgumentCount == -1 ? getArgumentCount() : minArgumentCount;
    }

    public void setMinArgumentCount(int minArgumentCount) {
        this.minArgumentCount = minArgumentCount;
    }

    @Override
    public int getArgumentCount() {
        if (minArgumentCount != -1 && minArgumentCount == argumentDefinitions.size() - 1 && argumentDefinitions.get(argumentDefinitions.size() - 1).getTypeDefinition() instanceof CollectionDomainTypeDefinition) {
            // Varargs
            return -1;
        } else {
            return argumentCount == -1 ? argumentDefinitions.size() : argumentCount;
        }
    }

    public void setArgumentCount(int argumentCount) {
        this.argumentCount = argumentCount;
        this.minArgumentCount = argumentCount;
    }

    @Override
    public String getResultTypeName() {
        return resultTypeName;
    }

    public void setResultTypeName(String resultTypeName) {
        this.resultTypeName = resultTypeName;
    }

    @Override
    public boolean isResultCollection() {
        return resultCollection;
    }

    public void setResultCollection(boolean resultCollection) {
        this.resultCollection = resultCollection;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DomainFunctionArgumentDefinition> getArgumentDefinitions() {
        return (List<DomainFunctionArgumentDefinition>) (List<?>) argumentDefinitions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DomainFunctionArgumentDefinitionImplementor> getArguments() {
        return (List<DomainFunctionArgumentDefinitionImplementor>) (List<?>) argumentDefinitions;
    }

    public DomainFunctionArgumentDefinitionImpl addArgumentDefinition(String name, String typeName, boolean collection) {
        if (positional == null) {
            positional = name == null || name.isEmpty();
        }
        if (positional && name != null && !name.isEmpty() || !positional && (name == null || name.isEmpty())) {
            throw new IllegalArgumentException("Can't mix positional and named parameters!");
        }
        DomainFunctionArgumentDefinitionImpl argumentDefinition = new DomainFunctionArgumentDefinitionImpl(this, name, argumentDefinitions.size(), typeName, collection);
        argumentDefinitions.add(argumentDefinition);
        return argumentDefinition;
    }

    public DomainFunctionArgumentDefinitionImpl addArgumentDefinition(DomainFunctionArgumentDefinition definition) {
        String name = definition.getName();
        if (positional == null) {
            positional = name == null || name.isEmpty();
        }
        if (positional && name != null && !name.isEmpty() || !positional && (name == null || name.isEmpty())) {
            throw new IllegalArgumentException("Can't mix positional and named parameters!");
        }
        DomainFunctionArgumentDefinitionImpl argumentDefinition = new DomainFunctionArgumentDefinitionImpl(this, definition);
        argumentDefinitions.add(argumentDefinition);
        return argumentDefinition;
    }

    @Override
    public DomainTypeDefinition getResultTypeDefinition() {
        return resultTypeDefinition;
    }

    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
        this.function = null;
        if (resultTypeName == null) {
            resultTypeDefinition = null;
        } else {
            if (resultCollection) {
                resultTypeDefinition = domainBuilder.getCollectionDomainTypeDefinition(resultTypeName);
            } else {
                resultTypeDefinition = domainBuilder.getDomainTypeDefinition(resultTypeName);
            }
            if (resultTypeDefinition == null) {
                context.addError("The result type '" + resultTypeName + "' defined for the function " + name + " is unknown!");
            }
        }

        for (DomainFunctionArgumentDefinitionImpl argumentDefinition : argumentDefinitions) {
            argumentDefinition.bindTypes(domainBuilder, context);
        }
    }

    public DomainFunction getFunction(MetamodelBuildingContext context) {
        if (function == null) {
            function = new DomainFunctionImpl(this, context);
        }

        return function;
    }
}
