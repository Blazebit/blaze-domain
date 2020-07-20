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

import com.blazebit.domain.runtime.model.BooleanLiteralResolver;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.CollectionLiteralResolver;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionTypeResolver;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainOperationTypeResolver;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.runtime.model.DomainPredicateTypeResolver;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.EntityDomainType;
import com.blazebit.domain.runtime.model.EntityLiteralResolver;
import com.blazebit.domain.runtime.model.EnumLiteralResolver;
import com.blazebit.domain.runtime.model.NumericLiteralResolver;
import com.blazebit.domain.runtime.model.StaticDomainFunctionTypeResolvers;
import com.blazebit.domain.runtime.model.StringLiteralResolver;
import com.blazebit.domain.runtime.model.TemporalLiteralResolver;
import com.blazebit.domain.spi.DomainSerializer;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainModelImpl implements DomainModel, Serializable {

    private final Map<String, Object> properties;
    private final Map<String, DomainType> domainTypes;
    private final Map<Class<?>, DomainType> domainTypesByJavaType;
    private final Map<DomainType, CollectionDomainType> collectionDomainTypes;
    private final Map<String, DomainFunction> domainFunctions;
    private final Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers;
    private final Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers;
    private final Map<Class<?>, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolversByJavaType;
    private final Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers;
    private final Map<Class<?>, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolversByJavaType;
    private final List<DomainSerializer<DomainModel>> domainSerializers;
    private final NumericLiteralResolver numericLiteralResolver;
    private final BooleanLiteralResolver booleanLiteralResolver;
    private final StringLiteralResolver stringLiteralResolver;
    private final TemporalLiteralResolver temporalLiteralResolver;
    private final EnumLiteralResolver enumLiteralResolver;
    private final EntityLiteralResolver entityLiteralResolver;
    private final CollectionLiteralResolver collectionLiteralResolver;

    public DomainModelImpl(Map<String, Object> properties, Map<String, DomainType> domainTypes, Map<Class<?>, DomainType> domainTypesByJavaType, Map<DomainType, CollectionDomainType> collectionDomainTypes, Map<String, DomainFunction> domainFunctions,
                           Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers, Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers, Map<Class<?>, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolversByJavaType,
                           Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers, Map<Class<?>, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolversByJavaType, List<DomainSerializer<DomainModel>> domainSerializers,
                           NumericLiteralResolver numericLiteralResolver, BooleanLiteralResolver booleanLiteralResolver, StringLiteralResolver stringLiteralResolver, TemporalLiteralResolver temporalLiteralResolver, EnumLiteralResolver enumLiteralResolver, EntityLiteralResolver entityLiteralResolver, CollectionLiteralResolver collectionLiteralResolver) {
        this.properties = Collections.unmodifiableMap(new HashMap<>(properties));
        this.domainTypes = domainTypes;
        this.domainTypesByJavaType = domainTypesByJavaType;
        this.collectionDomainTypes = collectionDomainTypes;
        this.domainFunctions = domainFunctions;
        this.domainFunctionTypeResolvers = domainFunctionTypeResolvers;
        this.domainOperationTypeResolvers = domainOperationTypeResolvers;
        this.domainOperationTypeResolversByJavaType = domainOperationTypeResolversByJavaType;
        this.domainPredicateTypeResolvers = domainPredicateTypeResolvers;
        this.domainPredicateTypeResolversByJavaType = domainPredicateTypeResolversByJavaType;
        this.domainSerializers = domainSerializers;
        this.numericLiteralResolver = numericLiteralResolver;
        this.booleanLiteralResolver = booleanLiteralResolver;
        this.stringLiteralResolver = stringLiteralResolver;
        this.temporalLiteralResolver = temporalLiteralResolver;
        this.enumLiteralResolver = enumLiteralResolver;
        this.entityLiteralResolver = entityLiteralResolver;
        this.collectionLiteralResolver = collectionLiteralResolver;
    }

    @Override
    public DomainType getType(String name) {
        return domainTypes.get(name);
    }

    @Override
    public DomainType getType(Class<?> javaType) {
        return domainTypesByJavaType.get(javaType);
    }

    @Override
    public EntityDomainType getEntityType(String name) {
        return (EntityDomainType) domainTypes.get(name);
    }

    @Override
    public EntityDomainType getEntityType(Class<?> javaType) {
        return (EntityDomainType) domainTypesByJavaType.get(javaType);
    }

    @Override
    public CollectionDomainType getCollectionType(DomainType elementDomainType) {
        return collectionDomainTypes.get(elementDomainType);
    }

    @Override
    public Map<String, DomainType> getTypes() {
        return domainTypes;
    }

    @Override
    public Map<Class<?>, DomainType> getTypesByJavaType() {
        return domainTypesByJavaType;
    }

    @Override
    public Map<DomainType, CollectionDomainType> getCollectionTypes() {
        return collectionDomainTypes;
    }

    @Override
    public DomainFunction getFunction(String name) {
        return domainFunctions.get(name.toUpperCase());
    }

    public Map<String, DomainFunction> getFunctions() {
        return domainFunctions;
    }

    @Override
    public DomainFunctionTypeResolver getFunctionTypeResolver(String functionName) {
        DomainFunctionTypeResolver typeResolver = domainFunctionTypeResolvers.get(functionName.toUpperCase());
        if (typeResolver == null) {
            return StaticDomainFunctionTypeResolvers.STATIC_RETURN_TYPE;
        }
        return typeResolver;
    }

    @Override
    public Map<String, DomainFunctionTypeResolver> getFunctionTypeResolvers() {
        return domainFunctionTypeResolvers;
    }

    @Override
    public DomainOperationTypeResolver getOperationTypeResolver(String typeName, DomainOperator operator) {
        Map<DomainOperator, DomainOperationTypeResolver> operationTypeResolverMap = domainOperationTypeResolvers.get(typeName);
        return operationTypeResolverMap == null ? null : operationTypeResolverMap.get(operator);
    }

    @Override
    public DomainOperationTypeResolver getOperationTypeResolver(Class<?> javaType, DomainOperator operator) {
        Map<DomainOperator, DomainOperationTypeResolver> operationTypeResolverMap = domainOperationTypeResolversByJavaType.get(javaType);
        return operationTypeResolverMap == null ? null : operationTypeResolverMap.get(operator);
    }

    @Override
    public DomainPredicateTypeResolver getPredicateTypeResolver(String typeName, DomainPredicate predicateType) {
        Map<DomainPredicate, DomainPredicateTypeResolver> predicateTypeResolverMap = domainPredicateTypeResolvers.get(typeName);
        return predicateTypeResolverMap == null ? null : predicateTypeResolverMap.get(predicateType);
    }

    @Override
    public DomainPredicateTypeResolver getPredicateTypeResolver(Class<?> javaType, DomainPredicate predicateType) {
        Map<DomainPredicate, DomainPredicateTypeResolver> predicateTypeResolverMap = domainPredicateTypeResolversByJavaType.get(javaType);
        return predicateTypeResolverMap == null ? null : predicateTypeResolverMap.get(predicateType);
    }

    @Override
    public Map<String, Map<DomainOperator, DomainOperationTypeResolver>> getOperationTypeResolvers() {
        return domainOperationTypeResolvers;
    }

    @Override
    public Map<Class<?>, Map<DomainOperator, DomainOperationTypeResolver>> getOperationTypeResolversByJavaType() {
        return domainOperationTypeResolversByJavaType;
    }

    @Override
    public Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> getPredicateTypeResolvers() {
        return domainPredicateTypeResolvers;
    }

    @Override
    public Map<Class<?>, Map<DomainPredicate, DomainPredicateTypeResolver>> getPredicateTypeResolversByJavaType() {
        return domainPredicateTypeResolversByJavaType;
    }

    @Override
    public NumericLiteralResolver getNumericLiteralResolver() {
        return numericLiteralResolver;
    }

    @Override
    public BooleanLiteralResolver getBooleanLiteralResolver() {
        return booleanLiteralResolver;
    }

    @Override
    public StringLiteralResolver getStringLiteralResolver() {
        return stringLiteralResolver;
    }

    @Override
    public TemporalLiteralResolver getTemporalLiteralResolver() {
        return temporalLiteralResolver;
    }

    @Override
    public EnumLiteralResolver getEnumLiteralResolver() {
        return enumLiteralResolver;
    }

    @Override
    public EntityLiteralResolver getEntityLiteralResolver() {
        return entityLiteralResolver;
    }

    @Override
    public CollectionLiteralResolver getCollectionLiteralResolver() {
        return collectionLiteralResolver;
    }

    @Override
    public List<DomainSerializer<DomainModel>> getDomainSerializers() {
        return domainSerializers;
    }

    @Override
    public <T> T serialize(Class<T> targetType, String format, Map<String, Object> properties) {
        for (DomainSerializer<DomainModel> domainSerializer : domainSerializers) {
            T result = domainSerializer.serialize(this, this, targetType, format, properties);
            if (result != null) {
                return result;
            }
        }

        return null;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }
}
