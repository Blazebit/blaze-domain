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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.DomainFunctionArgumentDefinition;
import com.blazebit.domain.boot.model.DomainFunctionDefinition;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.DomainFunctionImpl;
import com.blazebit.domain.runtime.model.DomainFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainFunctionDefinitionImpl extends MetadataDefinitionHolderImpl<DomainFunctionDefinition> implements DomainFunctionDefinitionImplementor {

    private final String name;
    private int minArgumentCount = -1;
    private int argumentCount = -1;
    private String resultTypeName;
    private Class<?> resultJavaType;
    private boolean collection;
    private Boolean positional;
    private List<DomainFunctionArgumentDefinitionImpl> argumentDefinitions = new ArrayList<>();
    private DomainTypeDefinition<?> resultTypeDefinition;
    private DomainFunction function;

    public DomainFunctionDefinitionImpl(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getMinArgumentCount() {
        return minArgumentCount == -1 ? argumentDefinitions.size() : minArgumentCount;
    }

    public void setMinArgumentCount(int minArgumentCount) {
        this.minArgumentCount = minArgumentCount;
    }

    @Override
    public int getArgumentCount() {
        return argumentCount == -1 ? argumentDefinitions.size() : argumentCount;
    }

    public void setArgumentCount(int argumentCount) {
        this.argumentCount = argumentCount;
        this.minArgumentCount = argumentCount;
    }

    public String getResultTypeName() {
        return resultTypeName;
    }

    public void setResultTypeName(String resultTypeName) {
        this.resultTypeName = resultTypeName;
    }

    public Class<?> getResultJavaType() {
        return resultJavaType;
    }

    public void setResultJavaType(Class<?> resultJavaType) {
        this.resultJavaType = resultJavaType;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
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

    public DomainFunctionArgumentDefinitionImpl addArgumentDefinition(String name, String typeName, Class<?> javaType, boolean collection) {
        if (positional == null) {
            if (name == null || name.isEmpty()) {
                positional = true;
            } else {
                positional = false;
            }
        }
        if (positional && name != null && !name.isEmpty() || !positional && (name == null || name.isEmpty())) {
            throw new IllegalArgumentException("Can't mix positional and named parameters!");
        }
        DomainFunctionArgumentDefinitionImpl argumentDefinition = new DomainFunctionArgumentDefinitionImpl(this, name, argumentDefinitions.size(), typeName, javaType, collection);
        argumentDefinitions.add(argumentDefinition);
        return argumentDefinition;
    }

    @Override
    public DomainTypeDefinition<?> getResultTypeDefinition() {
        return resultTypeDefinition;
    }

    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
        this.function = null;
        if (resultTypeName == null && resultJavaType == null) {
            resultTypeDefinition = null;
        } else {
            resultTypeDefinition = domainBuilder.getDomainTypeDefinition(resultTypeName);
            if (resultTypeDefinition == null) {
                resultTypeDefinition = domainBuilder.getDomainTypeDefinition(resultJavaType);
                if (resultTypeDefinition == null) {
                    context.addError("The result type '" + (resultTypeName == null ? resultJavaType.getName() : resultTypeName) + "' defined for the function " + name + " is unknown!");
                }
            }
            if (collection) {
                resultTypeDefinition = domainBuilder.getCollectionDomainTypeDefinition(resultTypeDefinition);
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
