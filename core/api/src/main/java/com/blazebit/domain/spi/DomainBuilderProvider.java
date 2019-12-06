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

package com.blazebit.domain.spi;

import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.runtime.model.DomainModel;

/**
 * Interface implemented by the domain implementation provider.
 *
 * Implementations are instantiated via {@link java.util.ServiceLoader}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainBuilderProvider {

    /**
     * Creates an empty domain builder.
     *
     * @return the domain builder
     */
    public DomainBuilder createEmptyBuilder();

    /**
     * Creates a domain builder based on an existing domain model.
     *
     * @param domainModel The existing domain model
     * @return the domain builder
     */
    public DomainBuilder createBuilder(DomainModel domainModel);

    /**
     * Creates an empty domain builder and returns it after running {@link DomainContributor} on it.
     *
     * @return the domain builder
     */
    public DomainBuilder createDefaultBuilder();

}
