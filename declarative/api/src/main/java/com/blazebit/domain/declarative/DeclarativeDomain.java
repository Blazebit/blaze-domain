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

package com.blazebit.domain.declarative;

import com.blazebit.domain.declarative.spi.DeclarativeDomainBuilderProvider;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Bootstrap class that is used to obtain a {@linkplain DeclarativeDomainConfiguration}.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public final class DeclarativeDomain {

    private static volatile DeclarativeDomainBuilderProvider cachedDefaultProvider;

    private DeclarativeDomain() {
    }

    /**
     * Returns the first {@linkplain DeclarativeDomainBuilderProvider} that is found.
     *
     * @return The first {@linkplain DeclarativeDomainBuilderProvider} that is found
     */
    public static DeclarativeDomainBuilderProvider getDefaultProvider() {
        DeclarativeDomainBuilderProvider defaultProvider = DeclarativeDomain.cachedDefaultProvider;
        if (defaultProvider == null) {
            ServiceLoader<DeclarativeDomainBuilderProvider> serviceLoader = ServiceLoader.load(DeclarativeDomainBuilderProvider.class);
            Iterator<DeclarativeDomainBuilderProvider> iterator = serviceLoader.iterator();

            if (iterator.hasNext()) {
                return DeclarativeDomain.cachedDefaultProvider = iterator.next();
            }

            throw new IllegalStateException("No DeclarativeDomainBuilderProvider found on the class path. Please check if a valid implementation is on the class path.");
        }
        return defaultProvider;
    }


}
