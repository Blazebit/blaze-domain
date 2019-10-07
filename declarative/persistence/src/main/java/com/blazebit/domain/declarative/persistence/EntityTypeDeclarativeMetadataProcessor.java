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

package com.blazebit.domain.declarative.persistence;

import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.declarative.spi.DeclarativeMetadataProcessor;

/**
 * A metadata processor for the {@linkplain EntityType} annotation.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EntityTypeDeclarativeMetadataProcessor implements DeclarativeMetadataProcessor<EntityType> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<EntityType> getProcessingAnnotation() {
        return EntityType.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MetadataDefinition<?> process(Class<?> annotatedClass, EntityType annotation) {
        return new EntityTypeImpl(annotation);
    }

}
