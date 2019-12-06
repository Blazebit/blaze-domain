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

package com.blazebit.domain.impl.runtime.model;

import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.impl.boot.model.EnumDomainTypeDefinitionImpl;
import com.blazebit.domain.impl.boot.model.EnumDomainTypeValueDefinitionImpl;
import com.blazebit.domain.impl.boot.model.MetamodelBuildingContext;
import com.blazebit.domain.runtime.model.EnumDomainType;
import com.blazebit.domain.runtime.model.EnumDomainTypeValue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EnumDomainTypeImpl extends AbstractDomainTypeImpl implements EnumDomainType {

    private final Map<String, EnumDomainTypeValue> enumValues;
    private final Map<Class<?>, Object> metadata;

    @SuppressWarnings("unchecked")
    public EnumDomainTypeImpl(EnumDomainTypeDefinitionImpl typeDefinition, MetamodelBuildingContext context) {
        super(typeDefinition, context);
        Map<String, EnumDomainTypeValue> enumValues;
        if (typeDefinition.isCaseSensitive()) {
            enumValues = new HashMap<>(typeDefinition.getEnumValues().size());
        } else {
            enumValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        }
        for (EnumDomainTypeValueDefinitionImpl enumValue : (Collection<EnumDomainTypeValueDefinitionImpl>) (Collection<?>) typeDefinition.getEnumValues().values()) {
            EnumDomainTypeValue old;
            if ((old = enumValues.put(enumValue.getValue(), enumValue.createValue(this, context))) != null) {
                context.addError("Duplicate enum value definition due to case insensitivity: [" + old.getValue() + ", " + enumValue.getValue() + "]");
            }
        }

        this.enumValues = enumValues;
        this.metadata = context.createMetadata(typeDefinition);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<? extends Enum<?>> getJavaType() {
        return (Class<? extends Enum<?>>) super.getJavaType();
    }

    @Override
    public Map<String, EnumDomainTypeValue> getEnumValues() {
        return enumValues;
    }

    @Override
    public DomainTypeKind getKind() {
        return DomainTypeKind.ENUM;
    }

    @Override
    public <T> T getMetadata(Class<T> metadataType) {
        return (T) metadata.get(metadataType);
    }

    @Override
    public Map<Class<?>, MetadataDefinition<?>> getMetadataDefinitions() {
        return getMetadataDefinitions(metadata);
    }
}
