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

import com.blazebit.domain.Domain;
import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.declarative.DeclarativeDomainConfiguration;
import com.blazebit.domain.declarative.spi.DeclarativeAttributeMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeDomainBuilderProvider;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeFunctionParameterMetadataProcessor;
import com.blazebit.domain.declarative.spi.DeclarativeMetadataProcessor;
import com.blazebit.domain.declarative.spi.TypeResolver;
import com.blazebit.domain.declarative.spi.TypeResolverDecorator;

import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeclarativeDomainBuilderProviderImpl implements DeclarativeDomainBuilderProvider {

    private static final Logger LOG = Logger.getLogger(DeclarativeDomainBuilderProviderImpl.class.getName());
    private static final ReferenceQueue<ClassLoader> REFERENCE_QUEUE = new ReferenceQueue<>();
    private static final ConcurrentMap<WeakClassLoaderKey, Providers> PROVIDERS = new ConcurrentHashMap<>();

    @Override
    public DeclarativeDomainConfiguration createEmptyBuilder() {
        return createEmptyBuilder(Domain.getDefaultProvider().createEmptyBuilder());
    }

    @Override
    public DeclarativeDomainConfiguration createEmptyBuilder(DomainBuilder builder) {
        return new DeclarativeDomainConfigurationImpl(builder);
    }

    @Override
    public DeclarativeDomainConfiguration createDefaultConfiguration() {
        return createDefaultConfiguration(null);
    }

    @Override
    public DeclarativeDomainConfiguration createDefaultConfiguration(DomainBuilder domainBuilder) {
        DeclarativeDomainConfigurationImpl domainConfiguration = new DeclarativeDomainConfigurationImpl(domainBuilder);
        Providers providers = getProviders();
        Iterator<TypeResolver> typeResolvers = providers.typeResolvers.iterator();
        if (typeResolvers.hasNext()) {
            TypeResolver typeResolver = typeResolvers.next();
            if (typeResolvers.hasNext()) {
                LOG.warning("Multiple type resolvers for declarative domain module are available! You will have to set a type resolver explicitly!");
            } else {
                domainConfiguration.setTypeResolver(typeResolver);
            }
        }
        for (DeclarativeMetadataProcessor<Annotation> processor : providers.declarativeMetadataProcessors) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (DeclarativeAttributeMetadataProcessor<Annotation> processor : providers.declarativeAttributeMetadataProcessors) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (DeclarativeFunctionMetadataProcessor<Annotation> processor : providers.declarativeFunctionMetadataProcessors) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (DeclarativeFunctionParameterMetadataProcessor<Annotation> processor : providers.declarativeFunctionParameterMetadataProcessors) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (TypeResolverDecorator typeResolverDecorator : providers.typeResolverDecorators) {
            domainConfiguration.withTypeResolverDecorator(typeResolverDecorator);
        }

        return domainConfiguration;
    }

    private static Providers getProviders() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = DeclarativeDomainBuilderProviderImpl.class.getClassLoader();
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
        private final Iterable<TypeResolver> typeResolvers;
        private final Iterable<DeclarativeMetadataProcessor<Annotation>> declarativeMetadataProcessors;
        private final Iterable<DeclarativeAttributeMetadataProcessor<Annotation>> declarativeAttributeMetadataProcessors;
        private final Iterable<DeclarativeFunctionMetadataProcessor<Annotation>> declarativeFunctionMetadataProcessors;
        private final Iterable<DeclarativeFunctionParameterMetadataProcessor<Annotation>> declarativeFunctionParameterMetadataProcessors;
        private final Iterable<TypeResolverDecorator> typeResolverDecorators;

        public Providers() {
            this.typeResolvers = load(TypeResolver.class);
            this.declarativeMetadataProcessors = load(DeclarativeMetadataProcessor.class);
            this.declarativeAttributeMetadataProcessors = load(DeclarativeAttributeMetadataProcessor.class);
            this.declarativeFunctionMetadataProcessors = load(DeclarativeFunctionMetadataProcessor.class);
            this.declarativeFunctionParameterMetadataProcessors = load(DeclarativeFunctionParameterMetadataProcessor.class);
            this.typeResolverDecorators = load(TypeResolverDecorator.class);
        }

        @SuppressWarnings("unchecked")
        private static <T> Iterable<T> load(Class<? super T> clazz) {
            return (Iterable<T>) StreamSupport.stream(ServiceLoader.load(clazz).spliterator(), false).collect(Collectors.toList());
        }
    }
}
