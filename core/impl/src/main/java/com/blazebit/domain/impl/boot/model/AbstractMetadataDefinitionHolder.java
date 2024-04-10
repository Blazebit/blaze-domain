/*
 * Copyright 2019 - 2024 Blazebit.
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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;
import com.blazebit.domain.impl.runtime.model.RuntimeMetadataDefinition;
import com.blazebit.domain.runtime.model.MetadataHolder;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public abstract class AbstractMetadataDefinitionHolder implements MetadataDefinitionHolder, Serializable {

    private Map<Class<?>, MetadataDefinition<?>> metadataDefinitions;

    public AbstractMetadataDefinitionHolder() {
    }

    public AbstractMetadataDefinitionHolder(MetadataDefinitionHolder metadataHolder) {
        if (!metadataHolder.getMetadataDefinitions().isEmpty()) {
            metadataDefinitions = new HashMap<>(metadataHolder.getMetadataDefinitions());
        }
    }

    public AbstractMetadataDefinitionHolder(MetadataHolder metadataHolder) {
        if (!metadataHolder.getMetadata().isEmpty()) {
            metadataDefinitions = new HashMap<>();
            for (Map.Entry<Class<?>, Object> entry : metadataHolder.getMetadata().entrySet()) {
                metadataDefinitions.put(entry.getKey(), new RuntimeMetadataDefinition(entry.getKey(), entry.getValue()));
            }
        }
    }

    public void withMetadataDefinition(MetadataDefinition<?> metadataDefinition) {
        if (metadataDefinitions == null) {
            metadataDefinitions = new HashMap<>();
        }
        metadataDefinitions.put(metadataDefinition.getJavaType(), metadataDefinition);
    }

    @Override
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions() {
        return metadataDefinitions == null ? Collections.emptyMap() : Collections.unmodifiableMap(metadataDefinitions);
    }
}
