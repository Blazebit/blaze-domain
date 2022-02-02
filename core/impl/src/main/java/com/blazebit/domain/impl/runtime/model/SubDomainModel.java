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
import com.blazebit.domain.spi.DomainSerializer;
import com.blazebit.domain.spi.ServiceProvider;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class SubDomainModel implements DomainModel, Serializable {

    private final DomainModel baseModel;
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

    public SubDomainModel(DomainModel baseModel, Map<String, Object> properties, Map<Class<?>, Object> services, List<ServiceProvider> serviceProviders, Map<String, DomainType> domainTypes, Map<DomainType, CollectionDomainType> collectionDomainTypes, Map<String, DomainFunction> domainFunctions,
                          Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers, Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers,
                          Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers, DomainType predicateDefaultResultType, List<DomainSerializer<?>> domainSerializers) {
        this.baseModel = baseModel;
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
        return baseModel;
    }

    @Override
    public DomainType getType(String name) {
        DomainType domainType = domainTypes.get(name);
        if (domainType == null) {
            return baseModel.getType(name);
        }
        return domainType;
    }

    @Override
    public EntityDomainType getEntityType(String name) {
        EntityDomainType entityDomainType = (EntityDomainType) domainTypes.get(name);
        if (entityDomainType == null) {
            return baseModel.getEntityType(name);
        }
        return entityDomainType;
    }

    @Override
    public EnumDomainType getEnumType(String name) {
        EnumDomainType enumDomainType = (EnumDomainType) domainTypes.get(name);
        if (enumDomainType == null) {
            return baseModel.getEnumType(name);
        }
        return enumDomainType;
    }

    @Override
    public CollectionDomainType getCollectionType(DomainType elementDomainType) {
        CollectionDomainType collectionDomainType = collectionDomainTypes.get(elementDomainType);
        if (collectionDomainType == null) {
            return baseModel.getCollectionType(elementDomainType);
        }
        return collectionDomainType;
    }

    @Override
    public Map<String, DomainType> getTypes() {
        Map<String, DomainType> types = baseModel.getTypes();
        Map<String, DomainType> map = new HashMap<>(types.size() + domainTypes.size());
        map.putAll(types);
        map.putAll(domainTypes);
        return map;
    }

    @Override
    public Map<DomainType, CollectionDomainType> getCollectionTypes() {
        Map<DomainType, CollectionDomainType> types = baseModel.getCollectionTypes();
        Map<DomainType, CollectionDomainType> map = new HashMap<>(types.size() + collectionDomainTypes.size());
        map.putAll(types);
        map.putAll(collectionDomainTypes);
        return map;
    }

    @Override
    public DomainFunction getFunction(String name) {
        DomainFunction domainFunction = domainFunctions.get(name.toUpperCase());
        if (domainFunction == null) {
            return baseModel.getFunction(name);
        }
        return domainFunction;
    }

    public Map<String, DomainFunction> getFunctions() {
        Map<String, DomainFunction> functions = baseModel.getFunctions();
        Map<String, DomainFunction> map = new HashMap<>(functions.size() + domainFunctions.size());
        map.putAll(functions);
        map.putAll(domainFunctions);
        return map;
    }

    @Override
    public DomainFunctionTypeResolver getFunctionTypeResolver(String functionName) {
        DomainFunctionTypeResolver typeResolver = domainFunctionTypeResolvers.get(functionName.toUpperCase());
        if (typeResolver == null) {
            return baseModel.getFunctionTypeResolver(functionName);
        }
        return typeResolver;
    }

    @Override
    public Map<String, DomainFunctionTypeResolver> getFunctionTypeResolvers() {
        Map<String, DomainFunctionTypeResolver> functionTypeResolvers = baseModel.getFunctionTypeResolvers();
        Map<String, DomainFunctionTypeResolver> map = new HashMap<>(functionTypeResolvers.size() + domainFunctionTypeResolvers.size());
        map.putAll(functionTypeResolvers);
        map.putAll(domainFunctionTypeResolvers);
        return map;
    }

    @Override
    public DomainOperationTypeResolver getOperationTypeResolver(String typeName, DomainOperator operator) {
        Map<DomainOperator, DomainOperationTypeResolver> operationTypeResolverMap = domainOperationTypeResolvers.get(typeName);
        DomainOperationTypeResolver resolver;
        if (operationTypeResolverMap == null || (resolver = operationTypeResolverMap.get(operator)) == null) {
            return baseModel.getOperationTypeResolver(typeName, operator);
        }
        return resolver;
    }

    @Override
    public DomainPredicateTypeResolver getPredicateTypeResolver(String typeName, DomainPredicate predicateType) {
        Map<DomainPredicate, DomainPredicateTypeResolver> predicateTypeResolverMap = domainPredicateTypeResolvers.get(typeName);
        DomainPredicateTypeResolver resolver;
        if (predicateTypeResolverMap == null || (resolver = predicateTypeResolverMap.get(predicateType)) == null) {
            return baseModel.getPredicateTypeResolver(typeName, predicateType);
        }
        return resolver;
    }

    @Override
    public Map<String, Map<DomainOperator, DomainOperationTypeResolver>> getOperationTypeResolvers() {
        Map<String, Map<DomainOperator, DomainOperationTypeResolver>> operationTypeResolvers = baseModel.getOperationTypeResolvers();
        Map<String, Map<DomainOperator, DomainOperationTypeResolver>> map = new HashMap<>(operationTypeResolvers.size() + domainOperationTypeResolvers.size());
        map.putAll(operationTypeResolvers);
        for (Map.Entry<String, Map<DomainOperator, DomainOperationTypeResolver>> entry : domainOperationTypeResolvers.entrySet()) {
            map.merge(entry.getKey(), entry.getValue(), (oldMap, newMap) -> {
                Map<DomainOperator, DomainOperationTypeResolver> subMap = new HashMap<>(oldMap.size() + newMap.size());
                subMap.putAll(oldMap);
                subMap.putAll(newMap);
                return Collections.unmodifiableMap(subMap);
            });
        }
        return map;
    }

    @Override
    public Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> getPredicateTypeResolvers() {
        Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> predicateTypeResolvers = baseModel.getPredicateTypeResolvers();
        Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> map = new HashMap<>(predicateTypeResolvers.size() + domainPredicateTypeResolvers.size());
        map.putAll(predicateTypeResolvers);
        for (Map.Entry<String, Map<DomainPredicate, DomainPredicateTypeResolver>> entry : domainPredicateTypeResolvers.entrySet()) {
            map.merge(entry.getKey(), entry.getValue(), (oldMap, newMap) -> {
                Map<DomainPredicate, DomainPredicateTypeResolver> subMap = new HashMap<>(oldMap.size() + newMap.size());
                subMap.putAll(oldMap);
                subMap.putAll(newMap);
                return Collections.unmodifiableMap(subMap);
            });
        }
        return map;
    }

    @Override
    public DomainType getPredicateDefaultResultType() {
        return predicateDefaultResultType;
    }

    @Override
    public List<DomainSerializer<?>> getDomainSerializers() {
        List<DomainSerializer<?>> baseModelDomainSerializers = baseModel.getDomainSerializers();
        if (domainSerializers.isEmpty() && baseModelDomainSerializers.isEmpty()) {
            return Collections.emptyList();
        }
        List<DomainSerializer<?>> list = new ArrayList<>(domainSerializers.size() + baseModelDomainSerializers.size());
        list.addAll(domainSerializers);
        list.addAll(baseModelDomainSerializers);
        return Collections.unmodifiableList(list);
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
        for (DomainSerializer<?> domainSerializer : baseModel.getDomainSerializers()) {
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
        return baseModel.getService(serviceClass);
    }
}
