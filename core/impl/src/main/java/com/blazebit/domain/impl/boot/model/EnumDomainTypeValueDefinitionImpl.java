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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.impl.runtime.model.EnumDomainTypeImpl;
import com.blazebit.domain.impl.runtime.model.EnumDomainTypeValueImpl;
import com.blazebit.domain.boot.model.EnumDomainTypeValueDefinition;
import com.blazebit.domain.runtime.model.EnumDomainTypeValue;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EnumDomainTypeValueDefinitionImpl extends MetadataDefinitionHolderImpl<EnumDomainTypeValueDefinition> implements EnumDomainTypeValueDefinition {

    private final EnumDomainTypeDefinitionImpl owner;
    private final String value;
    private EnumDomainTypeValue domainValue;

    public EnumDomainTypeValueDefinitionImpl(EnumDomainTypeDefinitionImpl owner, String value) {
        this.owner = owner;
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public EnumDomainTypeDefinitionImpl getOwner() {
        return owner;
    }

    public EnumDomainTypeValue createValue(EnumDomainTypeImpl enumDomainType, MetamodelBuildingContext context) {
        if (domainValue == null) {
            domainValue = new EnumDomainTypeValueImpl(enumDomainType, this, context);
        }
        return domainValue;
    }
}
