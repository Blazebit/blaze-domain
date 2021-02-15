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

package com.blazebit.domain.spi;

import com.blazebit.domain.boot.model.DomainBuilder;

/**
 * A {@link java.util.ServiceLoader} loaded contributor that adds common domain elements to a domain builder.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainContributor {

    /**
     * Contribute domain elements to the given domain builder.
     *
     * @param domainBuilder The domain builder to contribute domain elements to
     */
    void contribute(DomainBuilder domainBuilder);

    /**
     * Returns a priority(lower means higher priority) of the contributor.
     * The default priority is 1000.
     *
     * @return the priority
     * @since 1.0.12
     */
    default int priority() {
        return 1000;
    }

}
