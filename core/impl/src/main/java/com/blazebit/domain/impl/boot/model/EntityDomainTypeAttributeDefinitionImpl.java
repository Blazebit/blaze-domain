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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.boot.model.EntityDomainTypeAttributeDefinition;
import com.blazebit.domain.impl.runtime.model.EntityDomainTypeAttributeImpl;
import com.blazebit.domain.impl.runtime.model.EntityDomainTypeImpl;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.EntityDomainTypeAttribute;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public class EntityDomainTypeAttributeDefinitionImpl extends AbstractMetadataDefinitionHolder implements EntityDomainTypeAttributeDefinition {

    private final EntityDomainTypeDefinitionImpl owner;
    private final String name;
    private final String typeName;
    private final boolean collection;
    private DomainTypeDefinition typeDefinition;
    private EntityDomainTypeAttributeImpl attribute;

    public EntityDomainTypeAttributeDefinitionImpl(EntityDomainTypeDefinitionImpl owner, String name, String typeName, boolean collection) {
        this.owner = owner;
        this.name = name;
        this.typeName = typeName;
        this.collection = collection;
    }

    public EntityDomainTypeAttributeDefinitionImpl(EntityDomainTypeDefinitionImpl owner, EntityDomainTypeAttribute attribute) {
        super(attribute);
        this.owner = owner;
        this.name = attribute.getName();
        if (attribute.getType().getKind() == DomainType.DomainTypeKind.COLLECTION) {
            CollectionDomainType collectionDomainType = (CollectionDomainType) attribute.getType();
            if (collectionDomainType.getElementType() == null) {
                this.typeName = null;
            } else {
                this.typeName = collectionDomainType.getElementType().getName();
            }
            this.collection = true;
        } else {
            this.typeName = attribute.getType().getName();
            this.collection = false;
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public EntityDomainTypeDefinitionImpl getOwner() {
        return owner;
    }

    @Override
    public String getTypeName() {
        return typeName;
    }

    @Override
    public boolean isCollection() {
        return collection;
    }

    public DomainTypeDefinition getTypeDefinition() {
        return typeDefinition;
    }

    public void bindTypes(DomainBuilderImpl domainBuilder, MetamodelBuildingContext context) {
        this.attribute = null;
        if (typeName == null) {
            if (collection) {
                typeDefinition = domainBuilder.getCollectionDomainTypeDefinition(null);
            } else {
                typeDefinition = null;
            }
        } else {
            if (collection) {
                typeDefinition = domainBuilder.getCollectionDomainTypeDefinition(typeName);
            } else {
                typeDefinition = domainBuilder.getDomainTypeDefinition(typeName);
            }
        }
        if (typeDefinition == null) {
            context.addError("The type '" + typeName + "' defined for the attribute " + owner.getName() + "#" + name + " is unknown!");
        }
    }

    public EntityDomainTypeAttributeImpl createAttribute(EntityDomainTypeImpl entityDomainType, MetamodelBuildingContext context) {
        if (attribute == null) {
            attribute = new EntityDomainTypeAttributeImpl(entityDomainType, this, context);
        }
        return attribute;
    }
}
