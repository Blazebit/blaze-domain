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

package com.blazebit.domain.boot.model;

import java.util.Map;

/**
 * A domain element that can hold metadata definitions.
 *
 * @param <X> The self type for chaining
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface MetadataDefinitionHolder<X extends MetadataDefinitionHolder<X>> {

    /**
     * Adds the given metadata definition and returns <code>this</code> for chaining.
     *
     * @param metadataDefinition The metadata definition to add
     * @return this for chaining
     */
    public X withMetadataDefinition(MetadataDefinition<?> metadataDefinition);

    /**
     * Returns the metadata definitions of this domain element.
     *
     * @return the metadata definitions
     */
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions();
}
