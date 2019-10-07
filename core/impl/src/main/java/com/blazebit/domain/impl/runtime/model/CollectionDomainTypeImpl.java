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

import com.blazebit.domain.boot.model.CollectionDomainTypeDefinition;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicateType;
import com.blazebit.domain.runtime.model.DomainType;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class CollectionDomainTypeImpl extends AbstractDomainTypeImpl implements CollectionDomainType {

    private static final Set<DomainOperator> ENABLED_OPERATORS = EnumSet.noneOf(DomainOperator.class);
    private static final Set<DomainPredicateType> ENABLED_PREDICATES = EnumSet.of(DomainPredicateType.COLLECTION);
    private final DomainType elementType;
    private final Map<Class<?>, Object> metadata;

    public CollectionDomainTypeImpl(CollectionDomainTypeDefinition typeDefinition, MetamodelBuildingContext context) {
        super(typeDefinition, context);
        this.elementType = context.getType(typeDefinition.getElementType());
        this.metadata = context.createMetadata(typeDefinition);
    }

    @Override
    public DomainTypeKind getKind() {
        return DomainTypeKind.COLLECTION;
    }

    @Override
    public Set<DomainOperator> getEnabledOperators() {
        return ENABLED_OPERATORS;
    }

    @Override
    public Set<DomainPredicateType> getEnabledPredicates() {
        return ENABLED_PREDICATES;
    }

    @Override
    public DomainType getElementType() {
        return elementType;
    }

    @Override
    public <T> T getMetadata(Class<T> metadataType) {
        return (T) metadata.get(metadataType);
    }
}
