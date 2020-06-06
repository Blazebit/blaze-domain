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

package com.blazebit.domain.impl.spi;

import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.impl.boot.model.DomainBuilderImpl;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.spi.DomainBuilderProvider;
import com.blazebit.domain.spi.DomainContributor;
import com.blazebit.domain.spi.DomainSerializer;

import java.util.ServiceLoader;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class DomainBuilderProviderImpl implements DomainBuilderProvider {

    @Override
    public DomainBuilder createEmptyBuilder() {
        return new DomainBuilderImpl();
    }

    @Override
    public DomainBuilder createBuilder(DomainModel domainModel) {
        return new DomainBuilderImpl(domainModel);
    }

    @Override
    public DomainBuilder createDefaultBuilder() {
        DomainBuilderImpl domainBuilder = new DomainBuilderImpl();
        for (DomainContributor domainContributor : ServiceLoader.load(DomainContributor.class)) {
            domainContributor.contribute(domainBuilder);
        }
        for (DomainSerializer domainSerializer : ServiceLoader.load(DomainSerializer.class)) {
            domainBuilder.withSerializer(domainSerializer);
        }
        return domainBuilder;
    }
}
