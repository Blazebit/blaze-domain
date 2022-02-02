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

package com.blazebit.domain.impl.runtime.model;

import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;

import java.io.Serializable;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class RuntimeMetadataDefinition implements MetadataDefinition<Object>, Serializable {

    private final Class<?> type;
    private final Object object;

    public RuntimeMetadataDefinition(Class<?> type, Object object) {
        this.type = type;
        this.object = object;
    }

    @Override
    public Class<Object> getJavaType() {
        return (Class<Object>) type;
    }

    @Override
    public Object build(MetadataDefinitionHolder definitionHolder) {
        return object;
    }
}
