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

import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.boot.model.DomainFunctionArgumentDefinition;
import com.blazebit.domain.boot.model.DomainFunctionBuilder;
import com.blazebit.domain.boot.model.MetadataDefinition;

import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainFunctionBuilderImpl implements DomainFunctionBuilder {

    private final DomainBuilderImpl domainBuilder;
    private final DomainFunctionDefinitionImpl domainFunctionDefinition;

    public DomainFunctionBuilderImpl(DomainBuilderImpl domainBuilder, String name) {
        this.domainBuilder = domainBuilder;
        this.domainFunctionDefinition = new DomainFunctionDefinitionImpl(name);
    }

    @Override
    public String getName() {
        return domainFunctionDefinition.getName();
    }

    @Override
    public int getMinArgumentCount() {
        return domainFunctionDefinition.getMinArgumentCount();
    }

    @Override
    public int getArgumentCount() {
        return domainFunctionDefinition.getArgumentCount();
    }

    @Override
    public List<DomainFunctionArgumentDefinition> getArgumentDefinitions() {
        return domainFunctionDefinition.getArgumentDefinitions();
    }

    @Override
    public DomainFunctionBuilder withMinArgumentCount(int minArgumentCount) {
        domainFunctionDefinition.setMinArgumentCount(minArgumentCount);
        return this;
    }

    @Override
    public DomainFunctionBuilder withExactArgumentCount(int exactArgumentCount) {
        domainFunctionDefinition.setArgumentCount(exactArgumentCount);
        return this;
    }

    @Override
    public DomainFunctionBuilder withArgument(String name) {
        domainFunctionDefinition.addArgumentDefinition(name, null, null, false);
        return this;
    }

    @Override
    public DomainFunctionBuilder withArgument(String name, String typeName) {
        domainFunctionDefinition.addArgumentDefinition(name, typeName, null, false);
        return this;
    }

    @Override
    public DomainFunctionBuilder withCollectionArgument(String name) {
        domainFunctionDefinition.addArgumentDefinition(name, null, null, true);
        return this;
    }

    @Override
    public DomainFunctionBuilder withCollectionArgument(String name, String typeName) {
        domainFunctionDefinition.addArgumentDefinition(name, typeName, null, true);
        return this;
    }

    @Override
    public DomainFunctionBuilder withArgument(String name, MetadataDefinition<?>... metadataDefinitions) {
        return withArgument(name, null, metadataDefinitions);
    }

    @Override
    public DomainFunctionBuilder withArgument(String name, String typeName, MetadataDefinition<?>... metadataDefinitions) {
        DomainFunctionArgumentDefinitionImpl argumentDefinition = domainFunctionDefinition.addArgumentDefinition(name, typeName, null, false);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            argumentDefinition.withMetadataDefinition(metadataDefinition);
        }

        return this;
    }

    @Override
    public DomainFunctionBuilder withCollectionArgument(String name, String typeName, MetadataDefinition<?>... metadataDefinitions) {
        DomainFunctionArgumentDefinitionImpl argumentDefinition = domainFunctionDefinition.addArgumentDefinition(name, typeName, null, true);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            argumentDefinition.withMetadataDefinition(metadataDefinition);
        }

        return this;
    }

    @Override
    public DomainFunctionBuilder withCollectionArgument(String name, MetadataDefinition<?>... metadataDefinitions) {
        return withCollectionArgument(name, null, metadataDefinitions);
    }

    @Override
    public DomainFunctionBuilder withArgumentTypes(String... typeNames) {
        for (String typeName : typeNames) {
            domainFunctionDefinition.addArgumentDefinition(null, typeName, null, false);
        }
        return this;
    }

    @Override
    public DomainFunctionBuilder withResultType(String typeName) {
        domainFunctionDefinition.setResultTypeName(typeName);
        return this;
    }

    @Override
    public DomainFunctionBuilder withCollectionResultType() {
        domainFunctionDefinition.setCollection(true);
        return this;
    }

    @Override
    public DomainFunctionBuilder withCollectionResultType(String typeName) {
        domainFunctionDefinition.setResultTypeName(typeName);
        domainFunctionDefinition.setCollection(true);
        return this;
    }

    @Override
    public DomainFunctionBuilder withMetadata(MetadataDefinition<?> metadataDefinition) {
        domainFunctionDefinition.withMetadataDefinition(metadataDefinition);
        return this;
    }

    @Override
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions() {
        return domainFunctionDefinition.getMetadataDefinitions();
    }

    @Override
    public DomainBuilder build() {
        return domainBuilder.withDomainFunctionDefinition(domainFunctionDefinition);
    }
}
