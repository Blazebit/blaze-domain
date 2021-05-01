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

import com.blazebit.domain.boot.model.BasicDomainTypeDefinition;
import com.blazebit.domain.boot.model.CollectionDomainTypeDefinition;
import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.boot.model.DomainFunctionBuilder;
import com.blazebit.domain.boot.model.DomainFunctionDefinition;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.EntityDomainTypeAttributeDefinition;
import com.blazebit.domain.boot.model.EntityDomainTypeBuilder;
import com.blazebit.domain.boot.model.EntityDomainTypeDefinition;
import com.blazebit.domain.boot.model.EnumDomainTypeDefinition;
import com.blazebit.domain.boot.model.EnumDomainTypeValueDefinition;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.impl.runtime.model.RootDomainModel;
import com.blazebit.domain.impl.runtime.model.SubDomainModel;
import com.blazebit.domain.runtime.model.BasicDomainType;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;
import com.blazebit.domain.runtime.model.DomainFunctionTypeResolver;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainOperationTypeResolver;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.runtime.model.DomainPredicateTypeResolver;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.EntityDomainType;
import com.blazebit.domain.runtime.model.EntityDomainTypeAttribute;
import com.blazebit.domain.runtime.model.EnumDomainType;
import com.blazebit.domain.runtime.model.StaticDomainOperationTypeResolvers;
import com.blazebit.domain.runtime.model.StaticDomainPredicateTypeResolvers;
import com.blazebit.domain.spi.DomainContributor;
import com.blazebit.domain.spi.DomainSerializer;
import com.blazebit.domain.spi.ServiceProvider;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainBuilderImpl implements DomainBuilder, Serializable {

    private static final ReferenceQueue<ClassLoader> REFERENCE_QUEUE = new ReferenceQueue<>();
    private static final ConcurrentMap<WeakClassLoaderKey, Providers> PROVIDERS = new ConcurrentHashMap<>();

    private final DomainModel baseModel;
    private Map<String, Object> properties;
    private Map<Class<?>, Object> services;
    private List<ServiceProvider> serviceProviders;
    private Set<DomainSerializer<?>> domainSerializers;
    private final Map<String, DomainFunctionDefinitionImpl> domainFunctionDefinitions = new HashMap<>();
    private final Map<String, EnumSet<DomainOperator>> enabledOperators = new HashMap<>();
    private final Map<String, EnumSet<DomainPredicate>> enabledPredicates = new HashMap<>();
    private final Map<String, DomainTypeDefinitionImplementor> domainTypeDefinitions = new HashMap<>();
    private final Map<String, CollectionDomainTypeDefinitionImpl> collectionDomainTypeDefinitions = new HashMap<>();
    private final Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers = new HashMap<>();
    private final Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers = new HashMap<>();
    private final Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers = new HashMap<>();
    private boolean functionsCaseSensitive = true;
    private String predicateDefaultResultTypeName;
    private Set<DomainType> changedDomainTypes;

    public DomainBuilderImpl() {
        this.baseModel = null;
    }

    public DomainBuilderImpl(DomainModel domainModel) {
        this.baseModel = domainModel;
        DomainType type = baseModel.getPredicateDefaultResultType();
        this.predicateDefaultResultTypeName = type == null ? null : type.getName();
    }

    DomainModel getBaseModel() {
        return baseModel;
    }

    DomainBuilderImpl withDomainTypeDefinition(DomainTypeDefinitionImplementor domainTypeDefinition) {
        domainTypeDefinitions.put(domainTypeDefinition.getName(), domainTypeDefinition);
        DomainType baseDomainType;
        if (baseModel != null && (baseDomainType = baseModel.getType(domainTypeDefinition.getName())) != null) {
            addChangeDomainType(baseDomainType);
        }
        return this;
    }

    DomainBuilderImpl withDomainFunctionDefinition(DomainFunctionDefinitionImpl domainFunctionDefinition) {
        domainFunctionDefinitions.put(domainFunctionDefinition.getName(), domainFunctionDefinition);
        return this;
    }

    private void addChangeDomainType(DomainType baseDomainType) {
        if (changedDomainTypes == null) {
            changedDomainTypes = new HashSet<>();
        }
        changedDomainTypes.add(baseDomainType);
    }

    public DomainTypeDefinition getDomainTypeDefinition(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("Null type name!");
        }
        DomainTypeDefinition typeDefinition = domainTypeDefinitions.get(typeName);
        if (typeDefinition == null && typeName.startsWith("Collection[")) {
            typeDefinition = collectionDomainTypeDefinitions.get(typeName.substring("Collection[".length(), typeName.length() - 1));
        }
        if (typeDefinition == null && baseModel != null) {
            typeDefinition = (DomainTypeDefinition) baseModel.getType(typeName);
        }
        return typeDefinition;
    }

    public CollectionDomainTypeDefinitionImpl getCollectionDomainTypeDefinition(DomainTypeDefinition typeDefinition) {
        CollectionDomainTypeDefinitionImpl collectionDomainTypeDefinition;
        if (typeDefinition == null) {
            collectionDomainTypeDefinition = collectionDomainTypeDefinitions.get(null);
            if (collectionDomainTypeDefinition == null) {
                collectionDomainTypeDefinition = new CollectionDomainTypeDefinitionImpl("Collection", Collection.class, null);
                collectionDomainTypeDefinitions.put(null, collectionDomainTypeDefinition);
                withPredicate(collectionDomainTypeDefinition.getName(), DomainPredicate.COLLECTION);
            }
        } else {
            collectionDomainTypeDefinition = collectionDomainTypeDefinitions.get(typeDefinition.getName());
            if (collectionDomainTypeDefinition == null) {
                collectionDomainTypeDefinition = new CollectionDomainTypeDefinitionImpl("Collection[" + typeDefinition.getName() + "]", Collection.class, typeDefinition);
                collectionDomainTypeDefinitions.put(typeDefinition.getName(), collectionDomainTypeDefinition);
                withPredicate(collectionDomainTypeDefinition.getName(), DomainPredicate.COLLECTION);
            }
        }
        return collectionDomainTypeDefinition;
    }

    @Override
    public DomainBuilder withDefaults() {
        Providers providers = getProviders();
        for (DomainContributor domainContributor : providers.domainContributors) {
            domainContributor.contribute(this);
        }
        for (DomainSerializer<DomainModel> domainSerializer : providers.domainSerializers) {
            withSerializer(domainSerializer);
        }
        return this;
    }

    @Override
    public DomainBuilder withDefaultPredicateResultType(String typeName) {
        this.predicateDefaultResultTypeName = typeName;
        return this;
    }

    @Override
    public DomainBuilder withFunctionTypeResolver(String functionName, DomainFunctionTypeResolver functionTypeResolver) {
        domainFunctionTypeResolvers.put(functionName, functionTypeResolver);
        return this;
    }

    @Override
    public DomainBuilder withOperationTypeResolver(String typeName, DomainOperator domainOperator, DomainOperationTypeResolver operationTypeResolver) {
        domainOperationTypeResolvers.computeIfAbsent(typeName, k -> new HashMap<>()).put(domainOperator, operationTypeResolver);
        return this;
    }

    @Override
    public DomainBuilder withPredicateTypeResolver(String typeName, DomainPredicate domainPredicate, DomainPredicateTypeResolver predicateTypeResolver) {
        domainPredicateTypeResolvers.computeIfAbsent(typeName, k -> new HashMap<>()).put(domainPredicate, predicateTypeResolver);
        return this;
    }

    @Override
    public DomainOperationTypeResolver getOperationTypeResolver(String typeName, DomainOperator domainOperator) {
        Map<DomainOperator, DomainOperationTypeResolver> operationTypeResolverMap = domainOperationTypeResolvers.get(typeName);
        if (operationTypeResolverMap == null) {
            if (baseModel == null || (operationTypeResolverMap = baseModel.getOperationTypeResolvers().get(typeName)) == null) {
                return null;
            }
        }
        return operationTypeResolverMap.get(domainOperator);
    }

    @Override
    public DomainPredicateTypeResolver getPredicateTypeResolver(String typeName, DomainPredicate domainPredicate) {
        Map<DomainPredicate, DomainPredicateTypeResolver> predicateTypeResolverMap = domainPredicateTypeResolvers.get(typeName);
        if (predicateTypeResolverMap == null) {
            if (baseModel == null || (predicateTypeResolverMap = baseModel.getPredicateTypeResolvers().get(typeName)) == null) {
                return null;
            }
        }
        return predicateTypeResolverMap.get(domainPredicate);
    }

    @Override
    public DomainBuilder withOperator(String typeName, DomainOperator operator) {
        Set<DomainOperator> baseModelOperators = Collections.emptySet();
        DomainType baseModelDomainType = null;
        if (baseModel != null) {
            baseModelDomainType = baseModel.getType(typeName);
            if (baseModelDomainType != null) {
                baseModelOperators = baseModelDomainType.getEnabledOperators();
            }
        }
        if (withElement(enabledOperators, baseModelOperators, typeName, operator) && baseModel != null) {
            addChangeDomainType(baseModelDomainType);
        }
        return this;
    }

    @Override
    public DomainBuilder withPredicate(String typeName, DomainPredicate predicate) {
        Set<DomainPredicate> baseModelPredicates = Collections.emptySet();
        DomainType baseModelDomainType = null;
        if (baseModel != null) {
            baseModelDomainType = baseModel.getType(typeName);
            if (baseModelDomainType != null) {
                baseModelPredicates = baseModelDomainType.getEnabledPredicates();
            }
        }
        if (withElement(enabledPredicates, baseModelPredicates, typeName, predicate) && baseModel != null) {
            addChangeDomainType(baseModelDomainType);
        }
        return this;
    }

    @Override
    public DomainBuilder withOperator(String typeName, DomainOperator... operators) {
        Set<DomainOperator> baseModelOperators = Collections.emptySet();
        DomainType baseModelDomainType = null;
        if (baseModel != null) {
            baseModelDomainType = baseModel.getType(typeName);
            if (baseModelDomainType != null) {
                baseModelOperators = baseModelDomainType.getEnabledOperators();
            }
        }
        if (withElements(enabledOperators, baseModelOperators, typeName, operators) && baseModel != null) {
            addChangeDomainType(baseModelDomainType);
        }
        return this;
    }

    @Override
    public DomainBuilder withPredicate(String typeName, DomainPredicate... predicates) {
        Set<DomainPredicate> baseModelPredicates = Collections.emptySet();
        DomainType baseModelDomainType = null;
        if (baseModel != null) {
            baseModelDomainType = baseModel.getType(typeName);
            if (baseModelDomainType != null) {
                baseModelPredicates = baseModelDomainType.getEnabledPredicates();
            }
        }
        if (withElements(enabledPredicates, baseModelPredicates, typeName, predicates) && baseModel != null) {
            addChangeDomainType(baseModelDomainType);
        }
        return this;
    }

    @Override
    public Set<DomainOperator> getEnabledOperators(String typeName) {
        EnumSet<DomainOperator> domainOperators = enabledOperators.get(typeName);
        if (domainOperators == null) {
            DomainType baseType;
            if (baseModel == null || (baseType = baseModel.getType(typeName)) == null) {
                return Collections.emptySet();
            }
            return baseType.getEnabledOperators();
        }
        DomainType baseType;
        if (baseModel == null || (baseType = baseModel.getType(typeName)) == null) {
            return Collections.unmodifiableSet(domainOperators);
        }
        EnumSet<DomainOperator> set = EnumSet.copyOf(domainOperators);
        set.addAll(baseType.getEnabledOperators());
        return Collections.unmodifiableSet(set);
    }

    @Override
    public Set<DomainPredicate> getEnabledPredicates(String typeName) {
        EnumSet<DomainPredicate> domainPredicates = enabledPredicates.get(typeName);
        if (domainPredicates == null) {
            DomainType baseType;
            if (baseModel == null || (baseType = baseModel.getType(typeName)) == null) {
                return Collections.emptySet();
            }
            return baseType.getEnabledPredicates();
        }
        DomainType baseType;
        if (baseModel == null || (baseType = baseModel.getType(typeName)) == null) {
            return Collections.unmodifiableSet(domainPredicates);
        }
        EnumSet<DomainPredicate> set = EnumSet.copyOf(domainPredicates);
        set.addAll(baseType.getEnabledPredicates());
        return Collections.unmodifiableSet(set);
    }

    private <T extends Enum<T>> boolean withElement(Map<String, EnumSet<T>> map, Set<T> baseModelElements, String typeName, T element) {
        if (!baseModelElements.contains(element)) {
            EnumSet<T> set = map.get(typeName);
            if (set == null) {
                set = EnumSet.of(element);
                map.put(typeName, set);
            } else {
                set.add(element);
            }
            return true;
        }
        return false;
    }

    private <T extends Enum<T>> boolean withElements(Map<String, EnumSet<T>> map, Set<T> baseModelElements, String typeName, T... elements) {
        EnumSet<T> set = map.get(typeName);
        if (set == null) {
            set = EnumSet.noneOf((Class<T>) elements[0].getClass());
            if (!baseModelElements.isEmpty()) {
                for (int i = 0; i < elements.length; i++) {
                    T element = elements[i];
                    if (!baseModelElements.contains(element)) {
                        set.add(element);
                    }
                }
                if (set.isEmpty()) {
                    return false;
                }
                map.put(typeName, set);
                return true;
            }
            map.put(typeName, set);
        }

        for (int i = 0; i < elements.length; i++) {
            T element = elements[i];
            set.add(element);
        }

        return true;
    }

    @Override
    public DomainFunctionBuilder createFunction(String name) {
        return new DomainFunctionBuilderImpl(this, name);
    }

    @Override
    public DomainFunctionBuilder extendFunction(String name) {
        DomainFunctionDefinition functionDefinition = getFunction(name);
        if (functionDefinition == null) {
            throw new IllegalArgumentException("Function with name '" + name + "' does not exist!");
        }
        return new DomainFunctionBuilderImpl(this, functionDefinition);
    }

    @Override
    public DomainBuilder extendFunction(String name, MetadataDefinition<?>... metadataDefinitions) {
        if (metadataDefinitions != null && metadataDefinitions.length != 0) {
            DomainFunctionDefinition definition = getFunction(name);
            if (definition instanceof DomainFunctionDefinitionImpl) {
                DomainFunctionDefinitionImpl domainFunctionDefinition = (DomainFunctionDefinitionImpl) definition;
                for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
                    domainFunctionDefinition.withMetadataDefinition(metadataDefinition);
                }
            } else if (definition != null) {
                DomainFunctionBuilder builder = extendFunction(name);
                for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
                    builder.withMetadata(metadataDefinition);
                }
                builder.build();
            }
        }
        return this;
    }

    @Override
    public DomainBuilder createBasicType(String name) {
        return withDomainTypeDefinition(new BasicDomainTypeDefinitionImpl(name, null));
    }

    @Override
    public DomainBuilder createBasicType(String name, Class<?> javaType) {
        return withDomainTypeDefinition(new BasicDomainTypeDefinitionImpl(name, javaType));
    }

    @Override
    public DomainBuilder createBasicType(String name, MetadataDefinition<?>... metadataDefinitions) {
        BasicDomainTypeDefinitionImpl basicDomainTypeDefinition = new BasicDomainTypeDefinitionImpl(name, null);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            basicDomainTypeDefinition.withMetadataDefinition(metadataDefinition);
        }

        return withDomainTypeDefinition(basicDomainTypeDefinition);
    }

    @Override
    public DomainBuilder createBasicType(String name, Class<?> javaType, MetadataDefinition<?>... metadataDefinitions) {
        BasicDomainTypeDefinitionImpl basicDomainTypeDefinition = new BasicDomainTypeDefinitionImpl(name, javaType);
        for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
            basicDomainTypeDefinition.withMetadataDefinition(metadataDefinition);
        }

        return withDomainTypeDefinition(basicDomainTypeDefinition);
    }

    @Override
    public DomainBuilder extendBasicType(String name, MetadataDefinition<?>... metadataDefinitions) {
        if (metadataDefinitions != null && metadataDefinitions.length != 0) {
            DomainTypeDefinition definition = getType(name);
            if (definition instanceof BasicDomainTypeDefinition) {
                BasicDomainTypeDefinitionImpl basicTypeDefinition;
                if (definition instanceof BasicDomainTypeDefinitionImpl) {
                    basicTypeDefinition = (BasicDomainTypeDefinitionImpl) definition;
                } else {
                    basicTypeDefinition = new BasicDomainTypeDefinitionImpl((BasicDomainTypeDefinition) definition);
                    withDomainTypeDefinition(basicTypeDefinition);
                }
                for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
                    basicTypeDefinition.withMetadataDefinition(metadataDefinition);
                }
            } else if (definition != null) {
                throw new IllegalArgumentException("The type with the name '" + name + "' is not a basic type: " + definition);
            }
        }
        return this;
    }

    @Override
    public DomainBuilder extendBasicType(String name, Class<?> javaType, MetadataDefinition<?>... metadataDefinitions) {
        if (metadataDefinitions != null && metadataDefinitions.length != 0) {
            DomainTypeDefinition definition = getType(name);
            if (definition instanceof BasicDomainTypeDefinition) {
                BasicDomainTypeDefinitionImpl basicTypeDefinition;
                if (definition.getJavaType() == javaType && definition instanceof BasicDomainTypeDefinitionImpl) {
                    basicTypeDefinition = (BasicDomainTypeDefinitionImpl) definition;
                } else {
                    basicTypeDefinition = new BasicDomainTypeDefinitionImpl((BasicDomainTypeDefinition) definition, javaType);
                    withDomainTypeDefinition(basicTypeDefinition);
                }
                for (MetadataDefinition<?> metadataDefinition : metadataDefinitions) {
                    basicTypeDefinition.withMetadataDefinition(metadataDefinition);
                }
            } else if (definition != null) {
                throw new IllegalArgumentException("The type with the name '" + name + "' is not a basic type: " + definition);
            }
        }
        return this;
    }

    @Override
    public EntityDomainTypeBuilderImpl createEntityType(String name) {
        return new EntityDomainTypeBuilderImpl(this, name, null);
    }

    @Override
    public EntityDomainTypeBuilderImpl createEntityType(String name, Class<?> javaType) {
        return new EntityDomainTypeBuilderImpl(this, name, javaType);
    }

    @Override
    public EntityDomainTypeBuilder extendEntityType(String name, EntityDomainTypeDefinition baseEntityType) {
        EntityDomainTypeBuilderImpl builder = createEntityType(name);
        for (MetadataDefinition<?> metadataDefinition : baseEntityType.getMetadataDefinitions().values()) {
            builder.withMetadata(metadataDefinition);
        }

        for (EntityDomainTypeAttributeDefinition attribute : baseEntityType.getAttributes().values()) {
            builder.addAttribute(attribute.getName(), attribute.getTypeName(), attribute.getMetadataDefinitions().values().toArray(new MetadataDefinition[0]));
        }

        return builder;
    }

    @Override
    public EntityDomainTypeBuilder extendEntityType(String name, Class<?> javaType, EntityDomainTypeDefinition baseEntityType) {
        EntityDomainTypeBuilderImpl builder = createEntityType(name, javaType);
        for (MetadataDefinition<?> metadataDefinition : baseEntityType.getMetadataDefinitions().values()) {
            builder.withMetadata(metadataDefinition);
        }

        for (EntityDomainTypeAttributeDefinition attribute : baseEntityType.getAttributes().values()) {
            builder.addAttribute(attribute.getName(), attribute.getTypeName(), attribute.getMetadataDefinitions().values().toArray(new MetadataDefinition[0]));
        }

        return builder;
    }

    @Override
    public EnumDomainTypeBuilderImpl createEnumType(String name) {
        return new EnumDomainTypeBuilderImpl(this, name, null);
    }

    @Override
    public EnumDomainTypeBuilderImpl createEnumType(String name, Class<?> javaType) {
        return new EnumDomainTypeBuilderImpl(this, name, javaType);
    }

    @Override
    public EnumDomainTypeBuilderImpl extendEnumType(String name, EnumDomainTypeDefinition baseEnumType) {
        EnumDomainTypeBuilderImpl builder = createEnumType(name);
        for (MetadataDefinition<?> metadataDefinition : baseEnumType.getMetadataDefinitions().values()) {
            builder.withMetadata(metadataDefinition);
        }

        for (EnumDomainTypeValueDefinition enumValue : baseEnumType.getEnumValues().values()) {
            builder.withValue(enumValue.getValue(), enumValue.getMetadataDefinitions().values().toArray(new MetadataDefinition[0]));
        }

        return builder;
    }

    @Override
    public EnumDomainTypeBuilderImpl extendEnumType(String name, Class<?> javaType, EnumDomainTypeDefinition baseEnumType) {
        EnumDomainTypeBuilderImpl builder = createEnumType(name, javaType);
        for (MetadataDefinition<?> metadataDefinition : baseEnumType.getMetadataDefinitions().values()) {
            builder.withMetadata(metadataDefinition);
        }

        for (EnumDomainTypeValueDefinition enumValue : baseEnumType.getEnumValues().values()) {
            builder.withValue(enumValue.getValue(), enumValue.getMetadataDefinitions().values().toArray(new MetadataDefinition[0]));
        }

        return builder;
    }

    @Override
    public DomainTypeDefinition getType(String name) {
        DomainTypeDefinitionImplementor typeDefinition = domainTypeDefinitions.get(name);
        if (typeDefinition == null && baseModel != null) {
            typeDefinition = getType(baseModel.getType(name));
        }
        return typeDefinition;
    }

    @Override
    public EntityDomainTypeDefinition getEntityType(String name) {
        EntityDomainTypeDefinition typeDefinition = (EntityDomainTypeDefinition) domainTypeDefinitions.get(name);
        if (typeDefinition == null && baseModel != null) {
            typeDefinition = getEntityType(baseModel.getEntityType(name));
        }
        return typeDefinition;
    }

    @Override
    public CollectionDomainTypeDefinition getCollectionType(String elementDomainTypeName) {
        CollectionDomainTypeDefinition typeDefinition = collectionDomainTypeDefinitions.get(elementDomainTypeName);
        if (typeDefinition == null && baseModel != null) {
            typeDefinition = getCollectionType(baseModel.getCollectionType(baseModel.getType(elementDomainTypeName)));
        }
        return typeDefinition;
    }

    @Override
    public Map<String, DomainTypeDefinition> getTypes() {
        if (baseModel == null) {
            return (Map<String, DomainTypeDefinition>) (Map<?, ?>) domainTypeDefinitions;
        } else {
            Map<String, DomainType> types = baseModel.getTypes();
            Map<String, DomainTypeDefinition> map = new HashMap<>(types.size() + domainTypeDefinitions.size());
            for (Map.Entry<String, DomainType> entry : types.entrySet()) {
                map.put(entry.getKey(), getType(entry.getValue()));
            }
            map.putAll(domainTypeDefinitions);
            return map;
        }
    }

    @Override
    public Map<String, CollectionDomainTypeDefinition> getCollectionTypes() {
        if (baseModel == null) {
            return (Map<String, CollectionDomainTypeDefinition>) (Map<?, ?>) collectionDomainTypeDefinitions;
        } else {
            Map<DomainType, CollectionDomainType> collectionTypes = baseModel.getCollectionTypes();
            Map<String, CollectionDomainTypeDefinition> map = new HashMap<>(collectionTypes.size() + collectionDomainTypeDefinitions.size());
            for (Map.Entry<DomainType, CollectionDomainType> entry : collectionTypes.entrySet()) {
                map.put(entry.getKey().getName(), getCollectionType(entry.getValue()));
            }
            map.putAll(collectionDomainTypeDefinitions);
            return map;
        }
    }

    @Override
    public DomainFunctionDefinition getFunction(String name) {
        DomainFunctionDefinition domainFunctionDefinition = domainFunctionDefinitions.get(name);
        if (domainFunctionDefinition == null && baseModel != null) {
            domainFunctionDefinition = getDomainFunction(baseModel.getFunction(name));
        }
        return domainFunctionDefinition;
    }

    @Override
    public Map<String, DomainFunctionDefinition> getFunctions() {
        if (baseModel == null) {
            return (Map<String, DomainFunctionDefinition>) (Map<?, ?>) domainFunctionDefinitions;
        } else {
            Map<String, DomainFunction> functions = baseModel.getFunctions();
            Map<String, DomainFunctionDefinition> map = new HashMap<>(functions.size() + domainTypeDefinitions.size());
            for (Map.Entry<String, DomainFunction> entry : functions.entrySet()) {
                map.put(entry.getKey(), getDomainFunction(entry.getValue()));
            }
            map.putAll(domainFunctionDefinitions);
            return map;
        }
    }

    @Override
    public DomainBuilder setFunctionCaseSensitive(boolean caseSensitive) {
        this.functionsCaseSensitive = caseSensitive;
        return this;
    }

    @Override
    public DomainBuilder withSerializer(DomainSerializer<?> serializer) {
        if (domainSerializers == null) {
            if (baseModel == null) {
                domainSerializers = new LinkedHashSet<>();
            } else {
                domainSerializers = new LinkedHashSet<>(baseModel.getDomainSerializers());
            }
        }
        domainSerializers.add(serializer);
        return this;
    }

    private List<DomainSerializer<?>> getImmutableDomainSerializers() {
        if (domainSerializers != null && !domainSerializers.isEmpty()) {
            return Collections.unmodifiableList(new ArrayList<>(domainSerializers));
        } else if (baseModel != null) {
            return baseModel.getDomainSerializers();
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, Object> getProperties() {
        if (properties == null) {
            if (baseModel != null) {
                this.properties = new HashMap<>(baseModel.getProperties());
            } else {
                this.properties = new HashMap<>();
            }
        }
        return properties;
    }

    @Override
    public Object getProperty(String propertyName) {
        if (properties != null) {
            return properties.get(propertyName);
        } else if (baseModel != null) {
            return baseModel.getProperty(propertyName);
        }
        return null;
    }

    @Override
    public void setProperty(String propertyName, Object propertyValue) {
        if (properties == null) {
            if (baseModel != null) {
                if (baseModel.getProperty(propertyName) == propertyValue) {
                    return;
                }
                properties = new HashMap<>(baseModel.getProperties());
            } else {
                properties = new HashMap<>();
            }
        }
        properties.put(propertyName, propertyValue);
    }

    private Map<String, Object> getImmutableProperties() {
        if (properties != null && !properties.isEmpty()) {
            return Collections.unmodifiableMap(properties);
        } else if (baseModel != null) {
            return baseModel.getProperties();
        }
        return Collections.emptyMap();
    }

    @Override
    public Map<Class<?>, Object> getRegisteredServices() {
        if (services == null) {
            return Collections.emptyMap();
        }
        return services;
    }

    @Override
    public <T> DomainBuilder withService(Class<T> serviceClass, T service) {
        if (baseModel != null && baseModel.getService(serviceClass) == service && (services == null || !services.containsKey(serviceClass))) {
            return this;
        }
        if (services == null) {
            services = new HashMap<>();
        }
        services.put(serviceClass, service);
        return this;
    }

    @Override
    public <T> T getRegisteredService(Class<T> serviceClass) {
        if (services == null) {
            return null;
        }
        //noinspection unchecked
        return (T) services.get(serviceClass);
    }

    @Override
    public <T> T getService(Class<T> serviceClass) {
        if (services != null) {
            Object object = services.get(serviceClass);
            if (object != null) {
                return serviceClass.cast(object);
            }
        }
        if (serviceProviders != null) {
            for (ServiceProvider serviceProvider : serviceProviders) {
                T service = serviceProvider.getService(serviceClass);
                if (service != null) {
                    return service;
                }
            }
        }
        if (baseModel != null) {
            return baseModel.getService(serviceClass);
        }
        return null;
    }

    private Map<Class<?>, Object> getImmutableServices() {
        if (services == null || services.isEmpty()) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<>(services));
    }

    @Override
    public DomainBuilder withServiceProvider(ServiceProvider serviceProvider) {
        if (serviceProviders == null) {
            serviceProviders = new ArrayList<>();
        }
        serviceProviders.add(serviceProvider);
        return this;
    }

    private List<ServiceProvider> getImmutableServiceProviders() {
        if (serviceProviders == null || serviceProviders.isEmpty()) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(serviceProviders));
    }

    private DomainTypeDefinitionImplementor getType(DomainType type) {
        if (type instanceof DomainTypeDefinitionImplementor) {
            return (DomainTypeDefinitionImplementor) type;
        } else if (type instanceof EnumDomainType) {
            return new EnumDomainTypeDefinitionImpl((EnumDomainType) type);
        } else if (type instanceof EntityDomainType) {
            return new EntityDomainTypeDefinitionImpl((EntityDomainType) type);
        } else {
            return new BasicDomainTypeDefinitionImpl((BasicDomainType) type);
        }
    }

    private EntityDomainTypeDefinition getEntityType(EntityDomainType entityDomainType) {
        if (entityDomainType instanceof EntityDomainTypeDefinition) {
            return (EntityDomainTypeDefinition) entityDomainType;
        } else {
            return new EntityDomainTypeDefinitionImpl(entityDomainType);
        }
    }

    private CollectionDomainTypeDefinition getCollectionType(CollectionDomainType collectionDomainType) {
        if (collectionDomainType instanceof CollectionDomainTypeDefinition) {
            return (CollectionDomainTypeDefinition) collectionDomainType;
        } else {
            return new CollectionDomainTypeDefinitionImpl(collectionDomainType);
        }
    }

    private DomainFunctionDefinition getDomainFunction(DomainFunction domainFunction) {
        if (domainFunction instanceof DomainFunctionDefinition) {
            return (DomainFunctionDefinition) domainFunction;
        } else {
            return new DomainFunctionDefinitionImpl(domainFunction);
        }
    }

    private EntityDomainType[] getBaseModelEntityTypes(Set<DomainType> dependencies) {
        Collection<DomainType> domainTypes = baseModel.getTypes().values();
        List<EntityDomainType> entityDomainTypes = new ArrayList<>(domainTypes.size());
        for (DomainType domainType : domainTypes) {
            if (domainType instanceof EntityDomainType) {
                EntityDomainType entityDomainType = (EntityDomainType) domainType;
                if (!dependencies.contains(entityDomainType) && collectAttributeDependencies(entityDomainType, dependencies)) {
                    entityDomainTypes.add(entityDomainType);
                }
            }
        }
        return entityDomainTypes.toArray(entityDomainTypes.toArray(new EntityDomainType[0]));
    }

    private boolean collectAttributeDependencies(EntityDomainType entityDomainType, Set<DomainType> dependencies) {
        boolean hasEntityTypedAttributes = false;
        for (EntityDomainTypeAttribute attribute : entityDomainType.getAttributes().values()) {
            DomainType type = attribute.getType();
            if (type.getKind() == DomainType.DomainTypeKind.ENTITY) {
                hasEntityTypedAttributes = true;
            }
            if (dependencies.contains(type)) {
                dependencies.add(entityDomainType);
                CollectionDomainType collectionType = baseModel.getCollectionType(entityDomainType);
                if (collectionType != null) {
                    dependencies.add(collectionType);
                }
                // No need to check this type again as it is already in the dependencies
                return false;
            }
        }
        return hasEntityTypedAttributes;
    }

    private boolean dependsOn(DomainFunction domainFunction, Set<DomainType> dependencies) {
        if (dependencies.contains(domainFunction.getResultType())) {
            return true;
        }
        for (DomainFunctionArgument argument : domainFunction.getArguments()) {
            if (dependencies.contains(argument.getType())) {
                return true;
            }
        }

        return false;
    }

    private void handleChangedDomainTypes() {
        if (changedDomainTypes != null) {
            Set<DomainType> dependencies = new HashSet<>(changedDomainTypes);
            for (DomainType dependency : dependencies) {
                CollectionDomainType collectionType = baseModel.getCollectionType(dependency);
                if (collectionType != null) {
                    dependencies.add(collectionType);
                }
            }

            EntityDomainType[] entityDomainTypes = getBaseModelEntityTypes(dependencies);
            int lastSize = dependencies.size();
            do {
                int entityDomainTypesSize = entityDomainTypes.length;
                for (int i = 0; i < entityDomainTypesSize; i++) {
                    EntityDomainType entityDomainType = entityDomainTypes[i];
                    if (dependencies.contains(entityDomainType) || !collectAttributeDependencies(entityDomainType, dependencies)) {
                        // Remove this dependency by swapping last element in and decrementing size
                        entityDomainTypesSize--;
                        if (entityDomainTypesSize != 0) {
                            entityDomainTypes[i] = entityDomainTypes[entityDomainTypesSize];
                            i--;
                        }
                    }
                }
                if (dependencies.size() == lastSize) {
                    break;
                }
                lastSize = dependencies.size();
            } while (true);

            for (DomainType dependency : dependencies) {
                if (!domainTypeDefinitions.containsKey(dependency.getName())) {
                    DomainTypeDefinitionImplementor domainTypeDefinition;
                    if (dependency instanceof BasicDomainType) {
                        domainTypeDefinition = new BasicDomainTypeDefinitionImpl((BasicDomainType) dependency);
                    } else if (dependency instanceof EnumDomainType) {
                        domainTypeDefinition = new EnumDomainTypeDefinitionImpl((EnumDomainType) dependency);
                    } else if (dependency instanceof EntityDomainType) {
                        domainTypeDefinition = new EntityDomainTypeDefinitionImpl((EntityDomainType) dependency);
                    } else {
                        domainTypeDefinition = null;
                    }
                    if (domainTypeDefinition != null) {
                        domainTypeDefinitions.put(dependency.getName(), domainTypeDefinition);
                    }
                }
            }
            for (DomainFunction domainFunction : baseModel.getFunctions().values()) {
                if (!domainFunctionDefinitions.containsKey(domainFunction.getName()) && dependsOn(domainFunction, dependencies)) {
                    domainFunctionDefinitions.put(domainFunction.getName(), new DomainFunctionDefinitionImpl(domainFunction));
                }
            }
        }
    }

    @Override
    public DomainModel build() {
        MetamodelBuildingContext context = new MetamodelBuildingContext(this);
        handleChangedDomainTypes();
        for (DomainTypeDefinitionImplementor typeDefinition : domainTypeDefinitions.values()) {
            typeDefinition.bindTypes(this, context);
        }
        for (DomainFunctionDefinitionImpl domainFunctionDefinition : domainFunctionDefinitions.values()) {
            domainFunctionDefinition.bindTypes(this, context);
        }
        // Collection types might be added during type binding of functions or entity types so this must be done last
        for (CollectionDomainTypeDefinitionImpl collectionDomainTypeDefinition : collectionDomainTypeDefinitions.values()) {
            collectionDomainTypeDefinition.bindTypes(this, context);
        }
        Map<String, DomainType> domainTypes = new HashMap<>(domainTypeDefinitions.size());
        Map<DomainType, CollectionDomainType> collectionDomainTypes = new HashMap<>(domainTypeDefinitions.size());
        if (!context.hasErrors()) {
            for (DomainTypeDefinitionImplementor typeDefinition : domainTypeDefinitions.values()) {
                DomainType domainType = context.getType(typeDefinition);
                domainTypes.put(typeDefinition.getName(), domainType);
            }
            for (CollectionDomainTypeDefinitionImpl collectionDomainTypeDefinition : collectionDomainTypeDefinitions.values()) {
                CollectionDomainType collectionDomainType = collectionDomainTypeDefinition.getType(context);
                collectionDomainTypes.put(collectionDomainType.getElementType(), collectionDomainType);
                domainTypes.put(collectionDomainType.getName(), collectionDomainType);
            }
        }
        Map<String, DomainFunction> domainFunctions;
        if (functionsCaseSensitive) {
            domainFunctions = new HashMap<>(domainFunctionDefinitions.size());
        } else {
            domainFunctions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        if (!context.hasErrors()) {
            for (DomainFunctionDefinitionImpl functionDefinition : domainFunctionDefinitions.values()) {
                domainFunctions.put(functionDefinition.getName().toUpperCase(), functionDefinition.getFunction(context));
            }
        }
        Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers = new HashMap<>(this.domainFunctionTypeResolvers.size());
        if (!context.hasErrors()) {
            for (Map.Entry<String, DomainFunctionTypeResolver> entry : this.domainFunctionTypeResolvers.entrySet()) {
                String name = entry.getKey().toUpperCase();
                domainFunctionTypeResolvers.put(name, entry.getValue());
                if (!domainFunctions.containsKey(name)) {
                    context.addError("A function type resolver was registered but no function with the name '" + entry.getKey() + "' was found: " + entry.getValue());
                }
            }
        }

        Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers = new HashMap<>(this.domainOperationTypeResolvers.size());
        if (!context.hasErrors()) {
            resolveDomainOperationTypeResolvers(context, domainTypes, domainOperationTypeResolvers);
        }

        Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers = new HashMap<>(this.domainPredicateTypeResolvers.size());
        if (!context.hasErrors()) {
            resolveDomainPredicateTypeResolvers(context, domainTypes, domainPredicateTypeResolvers);
        }

        Map<String, Object> properties = getImmutableProperties();
        Map<Class<?>, Object> services = getImmutableServices();
        List<ServiceProvider> serviceProviders = getImmutableServiceProviders();
        DomainType predicateDefaultResultType;
        if (predicateDefaultResultTypeName == null) {
            predicateDefaultResultType = null;
        } else {
            predicateDefaultResultType = domainTypes.get(predicateDefaultResultTypeName);
            if (predicateDefaultResultType == null && baseModel != null) {
                predicateDefaultResultType = baseModel.getType(predicateDefaultResultTypeName);
            }
        }
        if (!context.hasErrors()) {
            for (DomainType domainType : domainTypes.values()) {
                Map<DomainOperator, DomainOperationTypeResolver> operationTypeResolverMap = domainOperationTypeResolvers.get(domainType.getName());
                if (operationTypeResolverMap == null && !domainType.getEnabledOperators().isEmpty()) {
                    operationTypeResolverMap = new HashMap<>();
                    domainOperationTypeResolvers.put(domainType.getName(), operationTypeResolverMap);
                }
                for (DomainOperator enabledOperator : domainType.getEnabledOperators()) {
                    if (!operationTypeResolverMap.containsKey(enabledOperator)) {
                        // TODO: Maybe throw an error instead?
                        operationTypeResolverMap.put(enabledOperator, StaticDomainOperationTypeResolvers.returning(domainType.getName()));
                    }
                }

                if (predicateDefaultResultType != null) {
                    Map<DomainPredicate, DomainPredicateTypeResolver> predicateTypeResolverMap = domainPredicateTypeResolvers.get(domainType.getName());
                    if (predicateTypeResolverMap == null && !domainType.getEnabledPredicates().isEmpty()) {
                        predicateTypeResolverMap = new HashMap<>();
                        domainPredicateTypeResolvers.put(domainType.getName(), predicateTypeResolverMap);
                    }
                    for (DomainPredicate enabledPredicate : domainType.getEnabledPredicates()) {
                        if (!predicateTypeResolverMap.containsKey(enabledPredicate)) {
                            predicateTypeResolverMap.put(enabledPredicate, StaticDomainPredicateTypeResolvers.returning(this.predicateDefaultResultTypeName));
                        }
                    }
                }
            }
        }

        if (context.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Couldn't build the domain model because of some errors:");
            for (String error : context.getErrors()) {
                sb.append('\n').append(error);
            }

            throw new IllegalArgumentException(sb.toString());
        }
        return createDomainModel(
            baseModel,
            properties,
            services,
            serviceProviders,
            domainTypes,
            collectionDomainTypes,
            domainFunctions,
            domainFunctionTypeResolvers,
            domainOperationTypeResolvers,
            domainPredicateTypeResolvers,
            predicateDefaultResultType,
            getImmutableDomainSerializers()
        );
    }

    private DomainModel createDomainModel(DomainModel baseModel, Map<String, Object> properties, Map<Class<?>, Object> services, List<ServiceProvider> serviceProviders, Map<String, DomainType> domainTypes, Map<DomainType, CollectionDomainType> collectionDomainTypes, Map<String, DomainFunction> domainFunctions,
                                          Map<String, DomainFunctionTypeResolver> domainFunctionTypeResolvers, Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers,
                                          Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers, DomainType predicateDefaultResultType, List<DomainSerializer<?>> domainSerializers) {
        if (baseModel == null) {
            return new RootDomainModel(
                properties,
                services,
                serviceProviders,
                domainTypes,
                collectionDomainTypes,
                domainFunctions,
                domainFunctionTypeResolvers,
                domainOperationTypeResolvers,
                domainPredicateTypeResolvers,
                predicateDefaultResultType,
                domainSerializers
            );
        } else {
            return new SubDomainModel(
                baseModel,
                properties,
                services,
                serviceProviders,
                domainTypes,
                collectionDomainTypes,
                domainFunctions,
                domainFunctionTypeResolvers,
                domainOperationTypeResolvers,
                domainPredicateTypeResolvers,
                predicateDefaultResultType,
                domainSerializers
            );
        }
    }

    private void resolveDomainOperationTypeResolvers(MetamodelBuildingContext context, Map<String, DomainType> domainTypes, Map<String, Map<DomainOperator, DomainOperationTypeResolver>> domainOperationTypeResolvers) {
        if (baseModel != null) {
            for (Map.Entry<String, Map<DomainOperator, DomainOperationTypeResolver>> entry : baseModel.getOperationTypeResolvers().entrySet()) {
                domainOperationTypeResolvers.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, Map<DomainOperator, DomainOperationTypeResolver>> entry : this.domainOperationTypeResolvers.entrySet()) {
            String typeName = entry.getKey();
            DomainType domainType = domainTypes.get(typeName);
            if (domainType == null) {
                context.addError("An operation type resolver was registered but no type with the name '" + typeName + "' was found: " + entry.getValue());
            } else {
                Map<DomainOperator, DomainOperationTypeResolver> operationTypeResolverMap = new HashMap<>(entry.getValue().size());
                domainOperationTypeResolvers.put(typeName, operationTypeResolverMap);

                for (Map.Entry<DomainOperator, DomainOperationTypeResolver> resolverEntry : entry.getValue().entrySet()) {
                    if (domainType.getEnabledOperators().contains(resolverEntry.getKey())) {
                        operationTypeResolverMap.put(resolverEntry.getKey(), resolverEntry.getValue());
                    } else {
                        context.addError("An operation type resolver for the type with the name '" + typeName + "' was registered for a non enabled operator '" + resolverEntry.getKey() + "': " + resolverEntry.getValue());
                    }
                }
            }
        }
    }

    private void resolveDomainPredicateTypeResolvers(MetamodelBuildingContext context, Map<String, DomainType> domainTypes, Map<String, Map<DomainPredicate, DomainPredicateTypeResolver>> domainPredicateTypeResolvers) {
        if (baseModel != null) {
            for (Map.Entry<String, Map<DomainPredicate, DomainPredicateTypeResolver>> entry : baseModel.getPredicateTypeResolvers().entrySet()) {
                domainPredicateTypeResolvers.put(entry.getKey(), entry.getValue());
            }
        }
        for (Map.Entry<String, Map<DomainPredicate, DomainPredicateTypeResolver>> entry : this.domainPredicateTypeResolvers.entrySet()) {
            String typeName = entry.getKey();
            DomainType domainType = domainTypes.get(typeName);
            if (domainType == null) {
                context.addError("An operation type resolver was registered but no type with the name '" + typeName + "' was found: " + entry.getValue());
            } else {
                Map<DomainPredicate, DomainPredicateTypeResolver> predicateTypeResolverMap = new HashMap<>(entry.getValue().size());
                domainPredicateTypeResolvers.put(typeName, predicateTypeResolverMap);

                for (Map.Entry<DomainPredicate, DomainPredicateTypeResolver> resolverEntry : entry.getValue().entrySet()) {
                    if (domainType.getEnabledPredicates().contains(resolverEntry.getKey())) {
                        predicateTypeResolverMap.put(resolverEntry.getKey(), resolverEntry.getValue());
                    } else {
                        context.addError("A predicate type resolver for the type with the name '" + typeName + "' was registered for a non enabled predicate '" + resolverEntry.getKey() + "': " + resolverEntry.getValue());
                    }
                }
            }
        }
    }

    private static Providers getProviders() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DomainBuilderImpl.class.getClassLoader();
        }
        // Cleanup old references
        Reference<? extends ClassLoader> reference;
        while ((reference = REFERENCE_QUEUE.poll()) != null) {
            PROVIDERS.remove(reference);
        }
        WeakClassLoaderKey key = new WeakClassLoaderKey(classLoader, REFERENCE_QUEUE);
        Providers providers = PROVIDERS.get(key);
        if (providers == null) {
            PROVIDERS.put(key, providers = new Providers());
        }
        return providers;
    }

    /**
     * @author Christian Beikov
     * @since 1.0.12
     */
    private static class WeakClassLoaderKey extends WeakReference<ClassLoader> {

        private final int hash;

        public WeakClassLoaderKey(ClassLoader referent, ReferenceQueue<ClassLoader> referenceQueue) {
            super(referent, referenceQueue);
            this.hash = referent.hashCode();
        }

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj instanceof WeakClassLoaderKey && ((WeakClassLoaderKey) obj).get() == get();
        }
    }

    /**
     * @author Christian Beikov
     * @since 1.0.12
     */
    private static class Providers {
        private final Iterable<DomainContributor> domainContributors;
        private final Iterable<DomainSerializer<DomainModel>> domainSerializers;

        public Providers() {
            domainContributors = StreamSupport.stream(ServiceLoader.load(DomainContributor.class).spliterator(), false)
                .sorted(Comparator.comparing(DomainContributor::priority))
                .collect(Collectors.toList());
            domainSerializers = load(DomainSerializer.class);
        }

        @SuppressWarnings("unchecked")
        private static <T> Iterable<T> load(Class<? super T> clazz) {
            return (Iterable<T>) StreamSupport.stream(ServiceLoader.load(clazz).spliterator(), false).collect(Collectors.toList());
        }
    }
}
