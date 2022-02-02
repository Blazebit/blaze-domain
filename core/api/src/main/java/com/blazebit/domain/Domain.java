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

package com.blazebit.domain;

import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.spi.DomainBuilderProvider;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Bootstrap class that is used to obtain a {@linkplain DomainModel}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class Domain {

    private static volatile DomainBuilderProvider cachedDefaultProvider;

    private Domain() {
    }

    /**
     * Returns the first {@linkplain DomainBuilderProvider} that is found.
     *
     * @return The first {@linkplain DomainBuilderProvider} that is found
     */
    public static DomainBuilderProvider getDefaultProvider() {
        DomainBuilderProvider defaultProvider = Domain.cachedDefaultProvider;
        if (defaultProvider == null) {
            ServiceLoader<DomainBuilderProvider> serviceLoader = ServiceLoader.load(DomainBuilderProvider.class);
            Iterator<DomainBuilderProvider> iterator = serviceLoader.iterator();

            if (iterator.hasNext()) {
                return Domain.cachedDefaultProvider = iterator.next();
            }

            throw new IllegalStateException("No DomainBuilderProvider found on the class path. Please check if a valid implementation is on the class path.");
        }
        return defaultProvider;
    }


}
