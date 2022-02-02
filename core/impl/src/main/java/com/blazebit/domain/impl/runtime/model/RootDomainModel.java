/*
 * Copyright 2019 - 2022 Blazebit.
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

import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionTypeResolver;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainOperationTypeResolver;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.runtime.model.DomainPredicateTypeResolver;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.EntityDomainType;
import com.blazebit.domain.runtime.model.EnumDomainType;
import com.blazebit.domain.runtime.model.StaticDomainFunctionTypeResolvers;
import com.blazebit.domain.spi.DomainSerializer;
import com.blazebit.domain.spi.ServiceProvider;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RootDomainModel implements DomainModel, Serializable {

    private final Map<String, Object> properties;
    private final Map<Class<?>, Object> services;
    private final List<ServiceProvider> serviceProviders;
    private final Map<String, DomainType> domainTypes;
    private final Map<DomainType, CollectionDomainType> collectionDomainTypes;
    private final Map<String, DomainFunction> domainFunctions;
    private final Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers;
    private final Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers;
    private final Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers;
    private final DomainType predicateDefaultResultType;
    private final List<DomainSerializer<?>> domainSerializers;

    public RootDomainModel(Map<String, Object> properties, Map<Class<?>, Object> services, List<ServiceProvider> serviceProviders, Map<String, DomainType> domainTypes, Map<DomainType, CollectionDomainType> collectionDomainTypes, Map<String, DomainFunction> domainFunctions,
                           Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers, Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers,
                           Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers, DomainType predicateDefaultResultType, List<DomainSerializer<?>> domainSerializers) {
        this.properties = properties;
        this.services = services;
        this.serviceProviders = serviceProviders;
        this.domainTypes = domainTypes;
        this.collectionDomainTypes = collectionDomainTypes;
        this.domainFunctions = domainFunctions;
        this.domainFunctionTypeResolvers = domainFunctionTypeResolvers;
        this.domainOperationTypeResolvers = domainOperationTypeResolvers;
        this.domainPredicateTypeResolvers = domainPredicateTypeResolvers;
        this.predicateDefaultResultType = predicateDefaultResultType;
        this.domainSerializers = domainSerializers;
    }

    @Override
    public DomainModel getParentDomainModel() {
        return null;
    }

    @Override
    public DomainType getType(String name) {
        return domainTypes.get(name);
    }

    @Override
    public EntityDomainType getEntityType(String name) {
        return (EntityDomainType) domainTypes.get(name);
    }

    @Override
    public EnumDomainType getEnumType(String name) {
        return (EnumDomainType) domainTypes.get(name);
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
    public DomainPredicateTypeResolver getPredicateTypeResolver(String typeName, DomainPredicate predicateType) {
        Map<DomainPredicate, DomainPredicateTypeResolver> predicateTypeResolverMap = domainPredicateTypeResolvers.get(typeName);
        return predicateTypeResolverMap == null ? null : predicateTypeResolverMap.get(predicateType);
    }

    @Override
    public Map<String, Map<DomainOperator, DomainOperationTypeResolver>> getOperationTypeResolvers() {
        return domainOperationTypeResolvers;
    }

    @Override
    public Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> getPredicateTypeResolvers() {
        return domainPredicateTypeResolvers;
    }

    @Override
    public DomainType getPredicateDefaultResultType() {
        return predicateDefaultResultType;
    }

    @Override
    public List<DomainSerializer<?>> getDomainSerializers() {
        return domainSerializers;
    }

    @Override
    public <T> T serialize(DomainModel baseModel, Class<T> targetType, String format, Map<String, Object> properties) {
        for (DomainSerializer<?> domainSerializer : domainSerializers) {
            if (domainSerializer.canSerialize(this)) {
                T result = ((DomainSerializer<DomainModel>) domainSerializer).serialize(this, baseModel, this, targetType, format, properties);
                if (result != null) {
                    return result;
                }
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

    @Override
    public <T> T getService(Class<T> serviceClass) {
        Object object = services.get(serviceClass);
        if (object != null) {
            return serviceClass.cast(object);
        }
        for (ServiceProvider serviceProvider : serviceProviders) {
            T service = serviceProvider.getService(serviceClass);
            if (service != null) {
                return service;
            }
        }
        return null;
    }
}
