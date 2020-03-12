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

package com.blazebit.domain.declarative.spi;

import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.declarative.DeclarativeDomainConfiguration;
import com.blazebit.domain.spi.DomainBuilderProvider;

/**
 * Interface implemented by the declarative domain implementation provider.
 *
 * Implementations are instantiated via {@link java.util.ServiceLoader}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DeclarativeDomainBuilderProvider {

    /**
     * Creates an empty declarative domain configuration.
     *
     * @return the declarative domain configuration
     */
    public DeclarativeDomainConfiguration createEmptyBuilder();

    /**
     * Creates an empty declarative domain configuration.
     *
     * @param builder The domain builder
     * @return the declarative domain configuration
     */
    public DeclarativeDomainConfiguration createEmptyBuilder(DomainBuilder builder);

    /**
     * Creates a declarative domain configuration based on {@link DomainBuilderProvider#createDefaultBuilder()} and
     * applies various SPI processors loaded via {@link java.util.ServiceLoader}.
     *
     * @return the declarative domain configuration
     */
    public DeclarativeDomainConfiguration createDefaultConfiguration();

    /**
     * Creates a declarative domain configuration based on {@link DomainBuilderProvider#createDefaultBuilder()} and
     * applies various SPI processors loaded via {@link java.util.ServiceLoader}.
     *
     * @param builder The domain builder
     * @return the declarative domain configuration
     */
    public DeclarativeDomainConfiguration createDefaultConfiguration(DomainBuilder builder);

}
