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

package com.blazebit.domain.declarative.impl.spi;

import com.blazebit.annotation.AnnotationUtils;
import com.blazebit.domain.Domain;
import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.boot.model.DomainFunctionBuilder;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.EntityDomainTypeBuilder;
import com.blazebit.domain.boot.model.EnumDomainTypeBuilder;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;
import com.blazebit.domain.declarative.DeclarativeDomainConfiguration;
import com.blazebit.domain.declarative.DiscoverMode;
import com.blazebit.domain.declarative.DomainAttribute;
import com.blazebit.domain.declarative.DomainFunction;
import com.blazebit.domain.declarative.DomainFunctionParam;
import com.blazebit.domain.declarative.DomainFunctions;
import com.blazebit.domain.declarative.DomainType;
import com.blazebit.domain.declarative.Metadata;
import com.blazebit.domain.declarative.MetadataType;
import com.blazebit.domain.declarative.Transient;
import com.blazebit.domain.declarative.spi.DeclarativeAttributeMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionParameterMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeMetadataProcessor;
import com.blazebit.domain.declarative.spi.TypeResolver;
import com.blazebit.domain.declarative.spi.TypeResolverDecorator;
import com.blazebit.domain.runtime.model.DomainFunctionTypeResolver;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.spi.ServiceProvider;
import com.blazebit.reflection.ReflectionUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeclarativeDomainConfigurationImpl implements DeclarativeDomainConfiguration {

    private static final MetadataDefinition[] EMPTY = new MetadataDefinition[0];
    private final Map<String, Object> properties = new HashMap<>();
    private final Map<Class<?>, Object> services = new HashMap<>();
    private final List<ServiceProvider> serviceProviders = new ArrayList<>();
    private final Map<Class<?>, DomainType> domainTypes = new HashMap<>();
    private final Map<Class<?>, DomainFunctions> domainFunctions = new HashMap<>();
    private final DomainBuilder domainBuilder;
    private final Map<Class<? extends Annotation>, List<DeclarativeMetadataProcessor<Annotation>>> entityMetadataProcessors = new HashMap<>();
    private final Map<Class<? extends Annotation>, List<DeclarativeAttributeMetadataProcessor<Annotation>>> attributeMetadataProcessors = new HashMap<>();
    private final Map<Class<? extends Annotation>, List<DeclarativeFunctionMetadataProcessor<Annotation>>> functionMetadataProcessors = new HashMap<>();
    private final Map<Class<? extends Annotation>, List<DeclarativeFunctionParameterMetadataProcessor<Annotation>>> functionParameterMetadataProcessors = new HashMap<>();
    private final List<TypeResolverDecorator> typeResolverDecorators = new ArrayList<>();
    private TypeResolver typeResolver;
    private boolean functionCaseSensitive;
    private transient TypeResolver configuredTypeResolver;
    private transient Map<Class<?>, String> domainTypeNamesByJavaTypes;

    public DeclarativeDomainConfigurationImpl() {
        this.domainBuilder = null;
    }

    public DeclarativeDomainConfigurationImpl(DomainBuilder domainBuilder) {
        this.domainBuilder = domainBuilder;
    }

    @Override
    public DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeMetadataProcessor<? extends Annotation> metadataProcessor) {
        entityMetadataProcessors.computeIfAbsent(metadataProcessor.getProcessingAnnotation(), k -> new ArrayList<>()).add((DeclarativeMetadataProcessor<Annotation>) metadataProcessor);
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeAttributeMetadataProcessor<? extends Annotation> metadataProcessor) {
        attributeMetadataProcessors.computeIfAbsent(metadataProcessor.getProcessingAnnotation(), k -> new ArrayList<>()).add((DeclarativeAttributeMetadataProcessor<Annotation>) metadataProcessor);
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeFunctionMetadataProcessor<? extends Annotation> metadataProcessor) {
        functionMetadataProcessors.computeIfAbsent(metadataProcessor.getProcessingAnnotation(), k -> new ArrayList<>()).add((DeclarativeFunctionMetadataProcessor<Annotation>) metadataProcessor);
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration withMetadataProcessor(DeclarativeFunctionParameterMetadataProcessor<? extends Annotation> metadataProcessor) {
        functionParameterMetadataProcessors.computeIfAbsent(metadataProcessor.getProcessingAnnotation(), k -> new ArrayList<>()).add((DeclarativeFunctionParameterMetadataProcessor<Annotation>) metadataProcessor);
        return this;
    }

    @Override
    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    @Override
    public DeclarativeDomainConfiguration setTypeResolver(TypeResolver typeResolver) {
        this.typeResolver = typeResolver;
        return this;
    }

    @Override
    public List<TypeResolverDecorator> getTypeResolverDecorators() {
        return typeResolverDecorators;
    }

    @Override
    public DeclarativeDomainConfiguration withTypeResolverDecorator(TypeResolverDecorator typeResolverDecorator) {
        typeResolverDecorators.add(typeResolverDecorator);
        return this;
    }

    @Override
    public <T> T getService(Class<T> serviceClass) {
        return (T) services.get(serviceClass);
    }

    @Override
    public Map<Class<?>, Object> getServices() {
        return Collections.unmodifiableMap(services);
    }

    @Override
    public <T> DeclarativeDomainConfiguration withService(Class<T> serviceClass, T service) {
        services.put(serviceClass, service);
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration withServiceProvider(ServiceProvider serviceProvider) {
        serviceProviders.add(serviceProvider);
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration setFunctionCaseSensitive(boolean caseSensitive) {
        functionCaseSensitive = caseSensitive;
        return this;
    }

    @Override
    public DomainModel createDomainModel() {
        DomainBuilder domainBuilder;
        if (this.domainBuilder == null) {
            domainBuilder = Domain.getDefaultProvider().createEmptyBuilder();
            registerPropertiesAndServices(domainBuilder);
            domainBuilder.withDefaults();
        } else {
            domainBuilder = this.domainBuilder;
            registerPropertiesAndServices(domainBuilder);
        }
        return createDomainModelInternal(domainBuilder);
    }

    @Override
    public DomainModel createDomainModel(DomainBuilder domainBuilder) {
        registerPropertiesAndServices(domainBuilder);
        return createDomainModelInternal(domainBuilder);
    }

    private void registerPropertiesAndServices(DomainBuilder domainBuilder) {
        for (Map.Entry<String, Object> entry : properties.entrySet()) {
            domainBuilder.setProperty(entry.getKey(), entry.getValue());
        }
        for (ServiceProvider serviceProvider : serviceProviders) {
            domainBuilder.withServiceProvider(serviceProvider);
        }
        for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
            //noinspection unchecked
            domainBuilder.withService((Class<Object>) entry.getKey(), entry.getValue());
        }
    }

    private DomainModel createDomainModelInternal(DomainBuilder domainBuilder) {
        domainBuilder.setFunctionCaseSensitive(functionCaseSensitive);
        TypeResolver r = typeResolver == null ? TypeResolver.NOOP : typeResolver;
        for (int i = 0; i < typeResolverDecorators.size(); i++) {
            r = typeResolverDecorators.get(i).decorate(r);
        }
        configuredTypeResolver = r;
        domainTypeNamesByJavaTypes = null;
        List<String> errors = new ArrayList<>();
        ServiceProvider serviceProvider = new ServiceProvider() {
            @Override
            public <T> T getService(Class<T> serviceClass) {
                if (serviceClass == DomainBuilder.class) {
                    return (T) domainBuilder;
                }
                Object service = services.get(serviceClass);
                if (service == null) {
                    for (ServiceProvider serviceProvider : serviceProviders) {
                        service = serviceProvider.getService(serviceClass);
                        if (service != null) {
                            break;
                        }
                    }
                }
                return (T) service;
            }
        };
        RuntimeException exception = null;
        try {
            analyzeDomainTypes(domainBuilder, serviceProvider, errors);
            analyzeDomainFunctions(domainBuilder, serviceProvider, errors);
        } catch (RuntimeException ex) {
            exception = ex;
        }
        if (!errors.isEmpty() || exception != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("There are error(s) the declarative domain configuration!");

            for (String error : errors) {
                sb.append('\n');
                sb.append(error);
            }

            throw new IllegalArgumentException(sb.toString(), exception);
        }
        return domainBuilder.build();
    }

    public Map<Class<?>, String> getDomainTypeNamesByJavaTypes(DomainBuilder domainBuilder) {
        if (domainTypeNamesByJavaTypes != null) {
            return domainTypeNamesByJavaTypes;
        }
        Collection<DomainTypeDefinition> typeDefinitions = domainBuilder.getTypes().values();
        domainTypeNamesByJavaTypes = new HashMap<>(typeDefinitions.size());
        for (DomainTypeDefinition typeDefinition : typeDefinitions) {
            if (typeDefinition.getJavaType() != null) {
                domainTypeNamesByJavaTypes.put(typeDefinition.getJavaType(), typeDefinition.getName());
            }
        }
        for (Map.Entry<Class<?>, DomainType> entry : domainTypes.entrySet()) {
            Class<?> domainTypeClass = entry.getKey();
            DomainType domainType = entry.getValue();
            if (domainType == null) {
                continue;
            }

            String name = domainType.value();
            if (name.isEmpty()) {
                name = domainTypeClass.getSimpleName();
            }
            domainTypeNamesByJavaTypes.put(entry.getKey(), name);
        }
        return domainTypeNamesByJavaTypes;
    }

    @Override
    public DeclarativeDomainConfiguration addDomainFunctions(Class<?> domainFunctionsClass) {
        domainFunctions.put(domainFunctionsClass, AnnotationUtils.findAnnotation(domainFunctionsClass, DomainFunctions.class));
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration addDomainFunctions(Class<?> domainFunctionsClass, DomainFunctions domainFunctionsAnnotation) {
        domainFunctions.put(domainFunctionsClass, domainFunctionsAnnotation);
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration removeDomainFunctions(Class<?> domainFunctionsClass) {
        domainFunctions.remove(domainFunctionsClass);
        return this;
    }

    @Override
    public Set<Class<?>> getDomainFunctions() {
        return domainFunctions.keySet();
    }

    @Override
    public DeclarativeDomainConfiguration addDomainType(Class<?> domainTypeClass) {
        domainTypes.put(domainTypeClass, AnnotationUtils.findAnnotation(domainTypeClass, DomainType.class));
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration addDomainType(Class<?> domainTypeClass, DomainType domainTypeAnnotation) {
        domainTypes.put(domainTypeClass, domainTypeAnnotation);
        return this;
    }

    @Override
    public DeclarativeDomainConfiguration removeDomainType(Class<?> domainTypeClass) {
        domainTypes.remove(domainTypeClass);
        return this;
    }

    @Override
    public Set<Class<?>> getDomainTypes() {
        return domainTypes.keySet();
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public Object getProperty(String propertyName) {
        return properties.get(propertyName);
    }

    public void analyzeDomainFunctions(DomainBuilder domainBuilder, ServiceProvider serviceProvider, List<String> errors) {
        for (Map.Entry<Class<?>, DomainFunctions> entry : domainFunctions.entrySet()) {
            Class<?> domainFunctionsClass = entry.getKey();
            DomainFunctions domainFunctions = entry.getValue();
            if (domainFunctions == null) {
                errors.add("No domain functions annotation found on type: " + domainFunctionsClass);
                continue;
            }

            boolean implicitDiscovery = domainFunctions.discoverMode() != DiscoverMode.EXPLICIT;
            Set<Class<?>> superTypes = ReflectionUtils.getSuperTypes(domainFunctionsClass);
            Set<String> handledMethods = new HashSet<>();
            superTypes.remove(Object.class);

            for (Class<?> c : superTypes) {
                for (Method method : c.getDeclaredMethods()) {
                    if (!method.isBridge() && method.getDeclaringClass() != Object.class) {
                        final String methodName = method.getName();
                        if (handledMethods.add(methodName)) {
                            handleDomainFunctionMethod(domainFunctionsClass, method, implicitDiscovery, domainBuilder, serviceProvider, errors);
                        }
                    }
                }
            }
        }
    }

    private void handleDomainFunctionMethod(Class<?> domainFunctionsClass, Method method, boolean implicitDiscovery, DomainBuilder domainBuilder, ServiceProvider serviceProvider, List<String> errors) {
        DomainFunction domainFunction = AnnotationUtils.findAnnotation(method, DomainFunction.class);
        if (domainFunction == null) {
            if (implicitDiscovery && AnnotationUtils.findAnnotation(method, Transient.class) == null) {
                domainFunction = new DomainFunctionLiteral();
            } else {
                return;
            }
        }

        String name = domainFunction.value();
        if (name.isEmpty()) {
            name = method.getName();
        }

        Class<?> type = domainFunction.collection() ? Collection.class : domainFunction.type();
        String typeName = domainFunction.collection() ? "Collection" : domainFunction.typeName();
        Class<?> elementType = domainFunction.collection() ? domainFunction.type() : void.class;
        String elementTypeName = domainFunction.collection() ? domainFunction.typeName() : "";

        DomainFunctionBuilder function = domainBuilder.createFunction(name);

        ResolvedType resolvedType = resolveType(domainBuilder, typeName, elementTypeName, type, elementType, domainFunctionsClass, method, null);
        String resolvedTypeName = null;
        boolean resolvedCollection = false;
        if (resolvedType != null) {
            resolvedTypeName = resolvedType.resolveTypeName(this, domainBuilder);
            if (resolvedType.collection) {
                resolvedCollection = true;
                function.withCollectionResultType(resolvedTypeName);
            } else {
                function.withResultType(resolvedTypeName);
            }
        }

        if (domainFunction.typeResolver() != DomainFunctionTypeResolver.class) {
            DomainFunctionTypeResolver functionTypeResolver = createInstance(domainFunction.typeResolver(), "function type resolver", errors);
            if (functionTypeResolver != null) {
                domainBuilder.withFunctionTypeResolver(name, functionTypeResolver);
            }
        }
        if (domainFunction.minArguments() > 0) {
            function.withMinArgumentCount(domainFunction.minArguments());
        }

        // automatic metadata discovery via meta annotations
        for (MetadataDefinition<?> metadataDefinition : getMetadataDefinitions(AnnotationUtils.findAnnotation(method, Metadata.class), errors)) {
            function.withMetadata(metadataDefinition);
        }
        for (Map.Entry<Class<? extends Annotation>, List<DeclarativeFunctionMetadataProcessor<Annotation>>> entry : functionMetadataProcessors.entrySet()) {
            if (entry.getKey() == null) {
                for (DeclarativeFunctionMetadataProcessor<Annotation> processor : entry.getValue()) {
                    MetadataDefinition<?> metadataDefinition = processor.process(domainFunctionsClass, method, null, name, resolvedTypeName, resolvedCollection, serviceProvider);
                    if (metadataDefinition != null) {
                        function.withMetadata(metadataDefinition);
                    }
                }
            } else {
                Annotation annotation = AnnotationUtils.findAnnotation(method, entry.getKey());
                if (annotation != null) {
                    for (DeclarativeFunctionMetadataProcessor<Annotation> processor : entry.getValue()) {
                        MetadataDefinition<?> metadataDefinition = processor.process(domainFunctionsClass, method, annotation, name, resolvedTypeName, resolvedCollection, serviceProvider);
                        if (metadataDefinition != null) {
                            function.withMetadata(metadataDefinition);
                        }
                    }
                }
            }
        }

        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            handleDomainFunctionParameter(domainBuilder, function, domainFunctionsClass, method, parameters[i], serviceProvider, errors);
        }
        function.build();
    }

    private String resolveJavaType(Class<?> type, Map<Class<?>, String> domainTypeNamesByJavaTypes) {
        String typeName = domainTypeNamesByJavaTypes.get(type);
        return typeName;
    }

    private void handleDomainFunctionParameter(DomainBuilder domainBuilder, DomainFunctionBuilder function, Class<?> domainFunctionsClass, Method method, Parameter parameter, ServiceProvider serviceProvider, List<String> errors) {
        String parameterName = parameter.getName();

        Class<?> type;
        String typeName;
        Class<?> elementType;
        String elementTypeName;
        DomainFunctionParam param = parameter.getAnnotation(DomainFunctionParam.class);
        if (param == null) {
            if (parameter.isVarArgs()) {
                type = Collection.class;
                typeName = "Collection";
                elementType = parameter.getType().getComponentType();
                elementTypeName = "";
            } else {
                type = void.class;
                typeName = "";
                elementType = null;
                elementTypeName = "";
            }
        } else {
            if (!param.value().isEmpty()) {
                parameterName = param.value();
            }

            if (param.collection() || parameter.isVarArgs()) {
                type = Collection.class;
                typeName = "Collection";
                elementType = param.type();
                elementTypeName = param.typeName();
            } else {
                type = param.type();
                typeName = param.typeName();
                elementType = void.class;
                elementTypeName = "";
            }
        }

        ResolvedType resolvedType = resolveType(domainBuilder, typeName, elementTypeName, type, elementType, domainFunctionsClass, method, parameter);
        String resolvedTypeName = null;
        boolean resolvedCollection;
        if (resolvedType == null) {
            resolvedCollection = param != null && param.collection();
        } else {
            resolvedTypeName = resolvedType.resolveTypeName(this, domainBuilder);
            resolvedCollection = resolvedType.collection;
        }

        // automatic metadata discovery via meta annotations
        List<MetadataDefinition<?>> metadataDefinitions = getMetadataDefinitions(parameter.getAnnotation(Metadata.class), errors);
        for (Map.Entry<Class<? extends Annotation>, List<DeclarativeFunctionParameterMetadataProcessor<Annotation>>> entry : functionParameterMetadataProcessors.entrySet()) {
            if (entry.getKey() == null) {
                for (DeclarativeFunctionParameterMetadataProcessor<Annotation> processor : entry.getValue()) {
                    MetadataDefinition<?> metadataDefinition = processor.process(domainFunctionsClass, method, parameter, null, parameterName, resolvedTypeName, resolvedCollection, serviceProvider);
                    if (metadataDefinition != null) {
                        if (metadataDefinition.getJavaType() == Transient.class) {
                            return;
                        }
                        metadataDefinitions.add(metadataDefinition);
                    }
                }
            } else {
                Annotation annotation = parameter.getAnnotation(entry.getKey());
                if (annotation != null) {
                    for (DeclarativeFunctionParameterMetadataProcessor<Annotation> processor : entry.getValue()) {
                        MetadataDefinition<?> metadataDefinition = processor.process(domainFunctionsClass, method, parameter, annotation, parameterName, resolvedTypeName, resolvedCollection, serviceProvider);
                        if (metadataDefinition != null) {
                            if (metadataDefinition.getJavaType() == Transient.class) {
                                return;
                            }
                            metadataDefinitions.add(metadataDefinition);
                        }
                    }
                }
            }
        }

        MetadataDefinition[] metadataDefinitionArray;
        if (metadataDefinitions.isEmpty()) {
            metadataDefinitionArray = EMPTY;
        } else {
            metadataDefinitionArray = metadataDefinitions.toArray(new MetadataDefinition[metadataDefinitions.size()]);
        }
        if (resolvedTypeName == null) {
            if (resolvedCollection) {
                function.withCollectionArgument(parameterName, metadataDefinitionArray);
            } else {
                function.withArgument(parameterName, metadataDefinitionArray);
            }
        } else {
            if (resolvedCollection) {
                function.withCollectionArgument(parameterName, resolvedTypeName, metadataDefinitionArray);
            } else {
                function.withArgument(parameterName, resolvedTypeName, metadataDefinitionArray);
            }
        }
        if (parameter.isVarArgs()) {
            function.withMinArgumentCount(function.getArgumentCount() - 1);
        }
    }

    private <X> X createInstance(Class<X> clazz, String kind, List<String> errors) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Exception ex) {
            StringWriter sw = new StringWriter();
            sw.write("Could not instantiate ");
            sw.write(kind);
            ex.printStackTrace(new PrintWriter(sw));
            errors.add(sw.toString());
            return null;
        }
    }

    private List<MetadataDefinition<?>> getMetadataDefinitions(Metadata annotation, List<String> errors) {
        List<MetadataDefinition<?>> list = new ArrayList<>();
        if (annotation != null) {
            for (Class<?> metadataObjectType : annotation.value()) {
                MetadataType metadataType = AnnotationUtils.findAnnotation(metadataObjectType, MetadataType.class);
                Class<Object> type;
                if (metadataType == null) {
                    type = (Class<Object>) metadataObjectType;
                } else {
                    type = (Class<Object>) metadataType.value();
                    if (!type.isAssignableFrom(metadataObjectType)) {
                        errors.add("The metadata object type '" + type.getName() + "' defined for '" + metadataObjectType.getName() + "' is not a super type!");
                        continue;
                    }
                }
                Object metadataObject = createInstance(metadataObjectType, "metadata object", errors);
                if (metadataObject != null) {
                    list.add(new SimpleMetadataDefinition<>(type, metadataObject));
                }
            }
        }
        return list;
    }

    private void analyzeDomainTypes(DomainBuilder domainBuilder, ServiceProvider serviceProvider, List<String> errors) {
        for (Map.Entry<Class<?>, DomainType> entry : domainTypes.entrySet()) {
            Class<?> domainTypeClass = entry.getKey();
            DomainType domainType = entry.getValue();
            if (domainType == null) {
                errors.add("No domain type annotation found on type: " + domainTypeClass);
                continue;
            }

            String name = domainType.value();
            if (name.isEmpty()) {
                name = domainTypeClass.getSimpleName();
            }

            boolean implicitDiscovery = domainType.discoverMode() != DiscoverMode.EXPLICIT;
            boolean caseSensitive = domainType.caseSensitive();
            Set<Class<?>> superTypes = ReflectionUtils.getSuperTypes(domainTypeClass);
            superTypes.remove(Object.class);

            // automatic metadata discovery via meta annotations
            List<MetadataDefinition<?>> metadataDefinitions = getMetadataDefinitions(AnnotationUtils.findAnnotation(domainTypeClass, Metadata.class), errors);

            for (Class<?> type : superTypes) {
                for (Map.Entry<Class<? extends Annotation>, List<DeclarativeMetadataProcessor<Annotation>>> metadataEntry : entityMetadataProcessors.entrySet()) {
                    if (metadataEntry.getKey() == null) {
                        for (DeclarativeMetadataProcessor<Annotation> processor : metadataEntry.getValue()) {
                            MetadataDefinition<?> metadataDefinition = processor.process(domainTypeClass, null, serviceProvider);
                            if (metadataDefinition != null) {
                                metadataDefinitions.add(metadataDefinition);
                            }
                        }
                    } else {
                        Annotation annotation = AnnotationUtils.findAnnotation(type, metadataEntry.getKey());
                        if (annotation != null) {
                            for (DeclarativeMetadataProcessor<Annotation> processor : metadataEntry.getValue()) {
                                MetadataDefinition<?> metadataDefinition = processor.process(domainTypeClass, annotation, serviceProvider);
                                if (metadataDefinition != null) {
                                    metadataDefinitions.add(metadataDefinition);
                                }
                            }
                        }
                    }
                }
            }

            if (domainTypeClass.isEnum()) {
                Class<? extends Enum<?>> enumDomainTypeClass = (Class<? extends Enum<?>>) domainTypeClass;
                EnumDomainTypeBuilder enumType = domainBuilder.createEnumType(name, enumDomainTypeClass);
                enumType.setCaseSensitive(caseSensitive);
                for (int i = 0; i < metadataDefinitions.size(); i++) {
                    enumType.withMetadata(metadataDefinitions.get(i));
                }
                Enum[] enumConstants = (Enum[]) domainTypeClass.getEnumConstants();
                for (int i = 0; i < (enumConstants).length; i++) {
                    handleEnumConstant(enumType, enumDomainTypeClass, enumConstants[i], serviceProvider, errors);
                }
                enumType.build();
            } else {
                EntityDomainTypeBuilder entityType = domainBuilder.createEntityType(name, domainTypeClass);
                entityType.setCaseSensitive(caseSensitive);
                for (int i = 0; i < metadataDefinitions.size(); i++) {
                    entityType.withMetadata(metadataDefinitions.get(i));
                }

                Set<String> handledMethods = new HashSet<>();

                for (Class<?> c : superTypes) {
                    for (Method method : c.getDeclaredMethods()) {
                        if (!Modifier.isPrivate(method.getModifiers()) && !method.isBridge()) {
                            final String methodName = method.getName();
                            if (handledMethods.add(methodName)) {
                                handleDomainAttributeMethod(domainBuilder, entityType, domainTypeClass, method, implicitDiscovery, serviceProvider, errors);
                            }
                        }
                    }
                }

                entityType.build();
            }

            domainBuilder.withPredicate(name, DomainPredicate.NULLNESS, DomainPredicate.EQUALITY);
        }
    }

    private void handleEnumConstant(EnumDomainTypeBuilder enumType, Class<? extends Enum<?>> domainTypeClass, Enum<?> enumConstant, ServiceProvider serviceProvider, List<String> errors) {
        // automatic metadata discovery via meta annotations
        List<MetadataDefinition<?>> metadataDefinitions = getMetadataDefinitions(enumConstant.getClass().getAnnotation(Metadata.class), errors);
        Class<? extends Enum> constantClass = enumConstant.getClass();
        for (Map.Entry<Class<? extends Annotation>, List<DeclarativeMetadataProcessor<Annotation>>> entry : entityMetadataProcessors.entrySet()) {
            if (entry.getKey() == null) {
                for (DeclarativeMetadataProcessor<Annotation> processor : entry.getValue()) {
                    MetadataDefinition<?> metadataDefinition = processor.process(constantClass, null, serviceProvider);
                    if (metadataDefinition != null) {
                        metadataDefinitions.add(metadataDefinition);
                    }
                }
            } else {
                Annotation annotation = AnnotationUtils.findAnnotation(constantClass, entry.getKey());
                if (annotation != null) {
                    for (DeclarativeMetadataProcessor<Annotation> processor : entry.getValue()) {
                        MetadataDefinition<?> metadataDefinition = processor.process(constantClass, annotation, serviceProvider);
                        if (metadataDefinition != null) {
                            metadataDefinitions.add(metadataDefinition);
                        }
                    }
                }
            }
        }

        MetadataDefinition[] metadataDefinitionArray;
        if (metadataDefinitions.isEmpty()) {
            metadataDefinitionArray = EMPTY;
        } else {
            metadataDefinitionArray = metadataDefinitions.toArray(new MetadataDefinition[metadataDefinitions.size()]);
        }

        enumType.withValue(enumConstant.name(), metadataDefinitionArray);
    }

    private void handleDomainAttributeMethod(DomainBuilder domainBuilder, EntityDomainTypeBuilder entityType, Class<?> domainTypeClass, Method method, boolean implicitDiscovery, ServiceProvider serviceProvider, List<String> errors) {
        DomainAttribute domainAttribute = AnnotationUtils.findAnnotation(method, DomainAttribute.class);
        if (domainAttribute == null) {
            if (implicitDiscovery && ReflectionUtils.isGetter(method) && AnnotationUtils.findAnnotation(method, Transient.class) == null) {
                domainAttribute = new DomainAttributeLiteral();
            } else {
                return;
            }
        } else if (!ReflectionUtils.isGetter(method)) {
            errors.add("Non-getter can't be a domain type attribute: " + method);
            return;
        }

        String name = getAttributeName(method);
        Class<?> type = domainAttribute.collection() ? Collection.class : domainAttribute.value();
        String typeName = domainAttribute.collection() ? "Collection" : domainAttribute.typeName();
        Class<?> elementType = domainAttribute.collection() ? domainAttribute.value() : void.class;
        String elementTypeName = domainAttribute.collection() ? domainAttribute.typeName() : "";
        ResolvedType resolvedType = resolveType(domainBuilder, typeName, elementTypeName, type, elementType, domainTypeClass, method, null);
        if (resolvedType == null) {
            resolvedType = ResolvedType.basic(Object.class);
        }
        String resolvedTypeName = resolvedType.resolveTypeName(this, domainBuilder);

        // automatic metadata discovery via meta annotations
        List<MetadataDefinition<?>> metadataDefinitions = getMetadataDefinitions(AnnotationUtils.findAnnotation(method, Metadata.class), errors);
        for (Map.Entry<Class<? extends Annotation>, List<DeclarativeAttributeMetadataProcessor<Annotation>>> entry : attributeMetadataProcessors.entrySet()) {
            if (entry.getKey() == DomainAttribute.class) {
                for (DeclarativeAttributeMetadataProcessor<Annotation> processor : entry.getValue()) {
                    MetadataDefinition<?> metadataDefinition = processor.process(domainTypeClass, method, domainAttribute, name, resolvedTypeName, resolvedType.collection, serviceProvider);
                    if (metadataDefinition != null) {
                        metadataDefinitions.add(metadataDefinition);
                    }
                }
            } else if (entry.getKey() == null) {
                for (DeclarativeAttributeMetadataProcessor<Annotation> processor : entry.getValue()) {
                    MetadataDefinition<?> metadataDefinition = processor.process(domainTypeClass, method, null, name, resolvedTypeName, resolvedType.collection, serviceProvider);
                    if (metadataDefinition != null) {
                        metadataDefinitions.add(metadataDefinition);
                    }
                }
            } else {
                Annotation annotation = AnnotationUtils.findAnnotation(method, entry.getKey());
                if (annotation != null) {
                    for (DeclarativeAttributeMetadataProcessor<Annotation> processor : entry.getValue()) {
                        MetadataDefinition<?> metadataDefinition = processor.process(domainTypeClass, method, annotation, name, resolvedTypeName, resolvedType.collection, serviceProvider);
                        if (metadataDefinition != null) {
                            metadataDefinitions.add(metadataDefinition);
                        }
                    }
                }
            }
        }

        MetadataDefinition[] metadataDefinitionArray;
        if (metadataDefinitions.isEmpty()) {
            metadataDefinitionArray = EMPTY;
        } else {
            metadataDefinitionArray = metadataDefinitions.toArray(new MetadataDefinition[metadataDefinitions.size()]);
        }
        if (resolvedType.collection) {
            entityType.addCollectionAttribute(name, resolvedTypeName, metadataDefinitionArray);
        } else {
            entityType.addAttribute(name, resolvedTypeName, metadataDefinitionArray);
        }
    }

    protected ResolvedType resolveType(DomainBuilder domainBuilder, String typeName, String elementTypeName, Class<?> type, Class<?> elementType, Class<?> baseClass, Method method, Parameter parameter) {
        if (!typeName.isEmpty()) {
            if ("Collection".equals(typeName)) {
                if (elementTypeName.isEmpty()) {
                    if (elementType != Object.class && elementType != void.class) {
                        return ResolvedType.collection(elementType);
                    }
                } else {
                    return ResolvedType.collection(elementTypeName);
                }
            } else {
                return ResolvedType.basic(typeName);
            }
        } else if (configuredTypeResolver != null) {
            Type t;
            if (type == void.class) {
                if (parameter == null) {
                    t = method.getGenericReturnType();
                } else {
                    t = parameter.getParameterizedType();
                }
            } else {
                t = type;
            }
            Object resolvedType = configuredTypeResolver.resolve(baseClass, t, domainBuilder);
            if (resolvedType == Object.class) {
                return null;
            }
            if (resolvedType == null && !(t instanceof Class<?>)) {
                // If the type could not be resolved, we try to resolve to a class type first and then invoke the resolver again
                if (parameter == null) {
                    resolvedType = configuredTypeResolver.resolve(baseClass, ReflectionUtils.getResolvedMethodReturnType(baseClass, method), domainBuilder);
                } else {
                    int idx = Arrays.asList(method.getParameters()).indexOf(parameter);
                    resolvedType = configuredTypeResolver.resolve(baseClass, ReflectionUtils.getResolvedMethodParameterTypes(baseClass, method)[idx], domainBuilder);
                }
            }
            if (resolvedType instanceof String) {
                return ResolvedType.basic((String) resolvedType);
            } else if (resolvedType instanceof Class<?>) {
                return ResolvedType.basic((Class<?>) resolvedType);
            } else if (resolvedType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) resolvedType;
                Type rawType = parameterizedType.getRawType();
                if ("Collection".equals(rawType.getTypeName()) || rawType instanceof Class<?> && Collection.class.isAssignableFrom((Class<?>) rawType)) {
                    Type[] typeArguments = parameterizedType.getActualTypeArguments();
                    if (typeArguments.length != 0 && !(typeArguments[0] instanceof WildcardType) && typeArguments[0] != Object.class) {
                        if (typeArguments[0] instanceof Class<?>) {
                            return ResolvedType.collection((Class<?>) typeArguments[0]);
                        } else {
                            return ResolvedType.collection(typeArguments[0].getTypeName());
                        }
                    }
                    return ResolvedType.collection();
                } else if (rawType instanceof Class<?>) {
                    return ResolvedType.basic((Class<?>) rawType);
                }
            }
        }

        Class<?> returnType;
        if (type == Object.class) {
            return null;
        } else if (type == void.class) {
            if (parameter == null) {
                returnType = ReflectionUtils.getResolvedMethodReturnType(baseClass, method);
            } else {
                returnType = parameter.getType();
            }
            elementType = resolveElementType(baseClass, method, parameter, returnType);
        } else {
            returnType = type;
            elementType = resolveElementType(baseClass, method, parameter, returnType);
        }
        if (Object.class == returnType) {
            return null;
        } else if (Collection.class.isAssignableFrom(returnType)) {
            if (configuredTypeResolver != null) {
                Object resolvedType = configuredTypeResolver.resolve(baseClass, elementType, domainBuilder);
                if (resolvedType instanceof String) {
                    return ResolvedType.collection((String) resolvedType);
                } else if (resolvedType instanceof Class<?>) {
                    return ResolvedType.collection((Class<?>) resolvedType);
                }
            }
            return ResolvedType.collection(elementType);
        } else {
            if (configuredTypeResolver != null) {
                Object resolvedType = configuredTypeResolver.resolve(baseClass, returnType, domainBuilder);
                if (resolvedType instanceof String) {
                    return ResolvedType.basic((String) resolvedType);
                } else if (resolvedType instanceof Class<?>) {
                    return ResolvedType.basic((Class<?>) resolvedType);
                }
            }
            return ResolvedType.basic(returnType);
        }
    }

    private Class<?> resolveElementType(Class<?> baseClass, Method method, Parameter parameter, Class<?> returnType) {
        Class<?> elementType;
        if (parameter == null) {
            if (Collection.class.isAssignableFrom(returnType)) {
                Class<?>[] typeArguments = ReflectionUtils.getResolvedMethodReturnTypeArguments(baseClass, method);
                elementType = typeArguments.length == 0 || typeArguments[0] == Object.class ? null : typeArguments[0];
            } else {
                elementType = null;
            }
        } else {
            if (Collection.class.isAssignableFrom(returnType)) {
                Type parameterType = parameter.getParameterizedType();
                elementType = null;
                if (parameterType instanceof ParameterizedType) {
                    Type[] typeArguments = ((ParameterizedType) parameterType).getActualTypeArguments();
                    Type resolvedType = ReflectionUtils.resolve(baseClass, typeArguments[0]);
                    if (resolvedType != Object.class) {
                        elementType = (Class<?>) resolvedType;
                    }
                }
            } else {
                elementType = null;
            }
        }
        return elementType;
    }

    /**
     * @param <X> The metadata type
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class SimpleMetadataDefinition<X> implements MetadataDefinition<X>, Serializable {

        private static final Field TYPE;

        static {
            try {
                Field field = SimpleMetadataDefinition.class.getDeclaredField("type");
                field.setAccessible(true);
                TYPE = field;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private final transient Class<X> type;
        private final X object;

        public SimpleMetadataDefinition(Class<X> type, X object) {
            this.type = type;
            this.object = object;
        }

        @Override
        public Class<X> getJavaType() {
            return type;
        }

        @Override
        public X build(MetadataDefinitionHolder definitionHolder) {
            return object;
        }

        private void writeObject(java.io.ObjectOutputStream out) throws IOException {
            out.defaultWriteObject();
            out.writeUTF(type.getName());
        }

        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            in.defaultReadObject();
            String className = in.readUTF();
            try {
                TYPE.set(this, Class.forName(className));
            } catch (Exception e) {
                throw new IOException(e);
            }
        }
    }

    /**
     * @author Christian Beikov
     * @since 1.0.0
     */
    private static class ResolvedType {
        private final String typeName;
        private final Class<?> type;
        private final boolean collection;

        public ResolvedType(String typeName, Class<?> type, boolean collection) {
            this.typeName = typeName;
            this.type = type;
            this.collection = collection;
        }

        public String resolveTypeName(DeclarativeDomainConfigurationImpl configuration, DomainBuilder domainBuilder) {
            if (typeName.isEmpty()) {
                return configuration.resolveJavaType(type, configuration.getDomainTypeNamesByJavaTypes(domainBuilder));
            } else {
                return typeName;
            }
        }

        public static ResolvedType basic(String typeName) {
            return new ResolvedType(typeName, null, false);
        }

        public static ResolvedType basic(Class<?> type) {
            return new ResolvedType("", type, false);
        }

        public static ResolvedType collection() {
            return new ResolvedType(null, null, true);
        }

        public static ResolvedType collection(String typeName) {
            return new ResolvedType(typeName, null, true);
        }

        public static ResolvedType collection(Class<?> type) {
            return new ResolvedType("", type, true);
        }
    }

    protected static String getAttributeName(Method getterOrSetter) {
        String name = getterOrSetter.getName();
        StringBuilder sb = new StringBuilder(name.length());
        int index = name.startsWith("is") ? 2 : 3;
        char firstAttributeNameChar = name.charAt(index);
        return sb.append(Character.toLowerCase(firstAttributeNameChar))
                .append(name, index + 1, name.length())
                .toString();
    }
}
