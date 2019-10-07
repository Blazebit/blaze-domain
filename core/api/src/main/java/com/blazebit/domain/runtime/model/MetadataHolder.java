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

package com.blazebit.domain.runtime.model;

/**
 * A domain element that can hold metadata definitions.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface MetadataHolder {

    /**
     * Returns the metadata object of the given class.
     *
     * @param metadataType The java type of the metadata object.
     * @param <T> The metadata type
     * @return The metadata object or <code>null</code>
     */
    public <T> T getMetadata(Class<T> metadataType);

}
