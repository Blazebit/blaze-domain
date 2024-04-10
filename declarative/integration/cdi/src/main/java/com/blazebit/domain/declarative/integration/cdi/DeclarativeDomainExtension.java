/*
 * Copyright 2019 - 2024 Blazebit.
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

package com.blazebit.domain.declarative.integration.cdi;

import com.blazebit.domain.declarative.DeclarativeDomain;
import com.blazebit.domain.declarative.DeclarativeDomainConfiguration;
import com.blazebit.domain.declarative.DomainFunctions;
import com.blazebit.domain.declarative.DomainType;
import com.blazebit.domain.spi.ServiceProvider;

import javax.enterprise.context.Dependent;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DeclarativeDomainExtension implements Extension {

    private final DeclarativeDomainConfiguration configuration = DeclarativeDomain.getDefaultProvider().createDefaultConfiguration();
    private final List<RuntimeException> exceptions = new ArrayList<>();

    <X> void processEntityView(@Observes @WithAnnotations({DomainType.class, DomainFunctions.class}) ProcessAnnotatedType<X> pat) {
        if (pat.getAnnotatedType().isAnnotationPresent(DomainType.class)) {
            try {
                configuration.addDomainType(pat.getAnnotatedType().getJavaClass());
            } catch (RuntimeException ex) {
                exceptions.add(new IllegalArgumentException("Exception occurred while reading domain type class: " + pat.getAnnotatedType().getJavaClass().getName(), ex));
            }
        }
        if (pat.getAnnotatedType().isAnnotationPresent(DomainFunctions.class)) {
            try {
                configuration.addDomainFunctions(pat.getAnnotatedType().getJavaClass());
            } catch (RuntimeException ex) {
                exceptions.add(new IllegalArgumentException("Exception occurred while reading domain type class: " + pat.getAnnotatedType().getJavaClass().getName(), ex));
            }
        }
    }
    
    void beforeBuild(@Observes AfterBeanDiscovery abd, BeanManager bm) {
        if (!exceptions.isEmpty()) {
            for (RuntimeException exception : exceptions) {
                abd.addDefinitionError(exception);
            }
            return;
        }
        Class<?> beanClass = DeclarativeDomainConfiguration.class;
        Class<?>[] types = new Class[] { DeclarativeDomainConfiguration.class, Object.class };
        Annotation[] qualifiers = new Annotation[] { new DefaultLiteral()};
        Class<? extends Annotation> scope = Dependent.class;
        Bean<DeclarativeDomainConfiguration> bean = new CustomBean<>(beanClass, types, qualifiers, scope, configuration);

        configuration.withServiceProvider(new BeanManagerServiceProvider(bm));
        abd.addBean(bean);
    }

    private static class BeanManagerServiceProvider implements ServiceProvider {

        private final BeanManager beanManager;

        public BeanManagerServiceProvider(BeanManager beanManager) {
            this.beanManager = beanManager;
        }

        @Override
        public <T> T getService(Class<T> serviceClass) {
            Set<Bean<?>> beans = beanManager.getBeans(serviceClass);
            if (beans.isEmpty()) {
                return null;
            }
            Bean<?> bean = beanManager.resolve(beans);
            CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
            return (T) beanManager.getReference(bean, serviceClass, creationalContext);
        }
    }
}
