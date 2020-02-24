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
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeclarativeDomainBuilderProviderImpl implements DeclarativeDomainBuilderProvider {

    private static final Logger LOG = Logger.getLogger(DeclarativeDomainBuilderProviderImpl.class.getName());

    @Override
    public DeclarativeDomainConfiguration createEmptyBuilder() {
        return new DeclarativeDomainConfigurationImpl(Domain.getDefaultProvider().createEmptyBuilder());
    }

    @Override
    public DeclarativeDomainConfiguration createDefaultConfiguration() {
        DomainBuilder domainBuilder = Domain.getDefaultProvider().createDefaultBuilder();
        DeclarativeDomainConfigurationImpl domainConfiguration = new DeclarativeDomainConfigurationImpl(domainBuilder);
        Iterator<TypeResolver> typeResolvers = ServiceLoader.load(TypeResolver.class).iterator();
        if (typeResolvers.hasNext()) {
            TypeResolver typeResolver = typeResolvers.next();
            if (typeResolvers.hasNext()) {
                LOG.warning("Multiple type resolvers for declarative domain module are available! You will have to set a type resolver explicitly!");
            } else {
                domainConfiguration.setTypeResolver(typeResolver);
            }
        }
        for (DeclarativeMetadataProcessor<Annotation> processor : ServiceLoader.load(DeclarativeMetadataProcessor.class)) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (DeclarativeAttributeMetadataProcessor<Annotation> processor : ServiceLoader.load(DeclarativeAttributeMetadataProcessor.class)) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (DeclarativeFunctionMetadataProcessor<Annotation> processor : ServiceLoader.load(DeclarativeFunctionMetadataProcessor.class)) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (DeclarativeFunctionParameterMetadataProcessor<Annotation> processor : ServiceLoader.load(DeclarativeFunctionParameterMetadataProcessor.class)) {
            domainConfiguration.withMetadataProcessor(processor);
        }
        for (TypeResolverDecorator typeResolverDecorator : ServiceLoader.load(TypeResolverDecorator.class)) {
            domainConfiguration.withTypeResolverDecorator(typeResolverDecorator);
        }

        return domainConfiguration;
    }
}
