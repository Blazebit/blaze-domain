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

import com.blazebit.domain.runtime.model.DomainModel;

import java.util.Map;

/**
 * A {@link java.util.ServiceLoader} loaded serializer that can serialize domain model elements.
 *
 * @param <X> The domain element type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainSerializer<X> {

    /**
     * Returns whether this serializer can serialize the element.
     *
     * @param element The element to check
     * @return Whether this serializer can serialize the element
     * @since 2.0.0
     */
    default boolean canSerialize(Object element) {
        return true;
    }

    /**
     * Serializes the domain model to the given target type with the given format.
     *
     * @param domainModel The domain model
     * @param element The domain element to serialize
     * @param targetType The target type
     * @param format The serialization format
     * @param properties Serialization properties
     * @param <T> The target type
     * @return The serialized form or <code>null</code> if the type or format is unsupported
     */
    public <T> T serialize(DomainModel domainModel, X element, Class<T> targetType, String format, Map<String, Object> properties);

    /**
     * Serializes the domain model to the given target type with the given format.
     * It only serializes elements that do not belong to the given base model already or are overridden.
     *
     * @param domainModel The domain model
     * @param baseModel The base domain model
     * @param element The domain element to serialize
     * @param targetType The target type
     * @param format The serialization format
     * @param properties Serialization properties
     * @param <T> The target type
     * @return The serialized form or <code>null</code> if the type or format is unsupported
     */
    default <T> T serialize(DomainModel domainModel, DomainModel baseModel, X element, Class<T> targetType, String format, Map<String, Object> properties) {
        return serialize(domainModel, element, targetType, format, properties);
    }

}
