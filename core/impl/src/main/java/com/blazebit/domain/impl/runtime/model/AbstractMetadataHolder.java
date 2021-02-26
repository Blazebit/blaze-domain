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

package com.blazebit.domain.impl.runtime.model;

import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;
import com.blazebit.domain.runtime.model.MetadataHolder;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public abstract class AbstractMetadataHolder implements MetadataHolder, MetadataDefinitionHolder, Serializable {

    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions(Map<Class<?>, Object> metadata) {
        if (metadata == null || metadata.isEmpty()) {
            return (Map<Class<?>, MetadataDefinition<?>>) (Map<?, ?>) metadata;
        }
        Map<Class<?>, MetadataDefinition<?>> map = new HashMap<>(metadata.size());
        for (Map.Entry<Class<?>, Object> entry : metadata.entrySet()) {
            map.put(entry.getKey(), new RuntimeMetadataDefinition(entry.getKey(), entry.getValue()));
        }
        return map;
    }
}
