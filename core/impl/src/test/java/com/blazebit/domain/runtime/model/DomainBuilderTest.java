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

package com.blazebit.domain.runtime.model;

import com.blazebit.domain.Domain;
import com.blazebit.domain.boot.model.DomainBuilder;
import com.blazebit.domain.boot.model.MetadataDefinition;
import com.blazebit.domain.boot.model.MetadataDefinitionHolder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;

public class DomainBuilderTest {

    private DomainBuilder createDefaultDomainBuilder() {
        DomainBuilder domainBuilder = Domain.getDefaultProvider().createDefaultBuilder();
        domainBuilder.createBasicType("String", String.class)
            .withOperator("String", DomainOperator.PLUS)
            .withPredicate("String", DomainPredicate.comparable());
        return domainBuilder;
    }

    @Test
    public void testBuildSimpleModel() {
        // Given 1
        DomainBuilder domainBuilder = createDefaultDomainBuilder();
        domainBuilder.createEntityType("Test")
                .addAttribute("name", "String", MetadataSample.INSTANCE)
                .withMetadata(MetadataSample.INSTANCE)
        .build();
        domainBuilder.createEntityType("TestContainer")
            .addAttribute("test", "Test", MetadataSample.INSTANCE)
            .withMetadata(MetadataSample.INSTANCE)
            .build();
        domainBuilder.createEntityType("Test2")
            .addAttribute("name", "String", MetadataSample.INSTANCE)
            .withMetadata(MetadataSample.INSTANCE)
            .build();
        domainBuilder.createEntityType("TestContainer2")
            .addAttribute("test", "Test2", MetadataSample.INSTANCE)
            .withMetadata(MetadataSample.INSTANCE)
            .build();

        // When 1
        DomainModel domainModel = domainBuilder.build();

        // Then 1
        EntityDomainType entityDomainType = (EntityDomainType) domainModel.getType("Test");
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getMetadata(MetadataSample.class));
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getAttribute("name").getMetadata(MetadataSample.class));
        Assert.assertEquals("String", entityDomainType.getAttribute("name").getType().getName());

        // Given 2
        domainBuilder = Domain.getDefaultProvider().createBuilder(domainModel);
        domainBuilder.extendEntityType("Test", domainBuilder.getEntityType("Test"))
            .addAttribute("name", "String", MetadataSample.INSTANCE)
            .addAttribute("name2", "String", MetadataSample.INSTANCE)
            .build();

        // When 2
        DomainModel domainModel2 = domainBuilder.build();

        // Then 2
        EntityDomainType entityDomainType2 = (EntityDomainType) domainModel2.getType("Test");
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType2.getMetadata(MetadataSample.class));
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType2.getAttribute("name2").getMetadata(MetadataSample.class));
        Assert.assertSame(domainModel.getType("String"), entityDomainType2.getAttribute("name").getType());
        Assert.assertNotSame(domainModel.getType("TestContainer"), domainModel2.getType("TestContainer"));
    }

    @Test
    public void testBuildCollectionModel() {
        // Given
        DomainBuilder domainBuilder = createDefaultDomainBuilder();
        domainBuilder.createEntityType("Test")
                .addCollectionAttribute("names", "String", MetadataSample.INSTANCE)
                .addCollectionAttribute("objects", MetadataSample.INSTANCE)
                .withMetadata(MetadataSample.INSTANCE)
                .build();
        domainBuilder.createFunction("size")
            .withCollectionArgument("collection")
            .withResultType("String")
            .build();
        // When
        DomainModel domainModel = domainBuilder.build();

        // Then
        EntityDomainType entityDomainType = (EntityDomainType) domainModel.getType("Test");
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getMetadata(MetadataSample.class));
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getAttribute("names").getMetadata(MetadataSample.class));
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getAttribute("objects").getMetadata(MetadataSample.class));
        Assert.assertEquals("Collection[String]", entityDomainType.getAttribute("names").getType().getName());
        Assert.assertEquals("Collection", entityDomainType.getAttribute("objects").getType().getName());
        Assert.assertEquals("String", ((CollectionDomainType) entityDomainType.getAttribute("names").getType()).getElementType().getName());
        Assert.assertNull(((CollectionDomainType) entityDomainType.getAttribute("objects").getType()).getElementType());
    }

    @Test
    public void testBuildEnumModel() {
        // Given
        DomainBuilder domainBuilder = createDefaultDomainBuilder();
        domainBuilder.createEnumType("TestKind")
                .withValue("UnitTest", MetadataSample.INSTANCE)
                .withValue("IntegrationTest", MetadataSample.INSTANCE)
                .withMetadata(MetadataSample.INSTANCE)
                .build();
        domainBuilder.createEntityType("Test")
                .addAttribute("kind", "TestKind", MetadataSample.INSTANCE)
                .withMetadata(MetadataSample.INSTANCE)
                .build();

        // When
        DomainModel domainModel = domainBuilder.build();

        // Then
        EntityDomainType entityDomainType = (EntityDomainType) domainModel.getType("Test");
        EnumDomainType enumDomainType = (EnumDomainType) domainModel.getType("TestKind");
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getMetadata(MetadataSample.class));
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getAttribute("kind").getType().getMetadata(MetadataSample.class));
        Assert.assertEquals(MetadataSample.INSTANCE, enumDomainType.getEnumValues().get("UnitTest").getMetadata(MetadataSample.class));
        Assert.assertEquals("TestKind", entityDomainType.getAttribute("kind").getType().getName());
        Assert.assertEquals(2, ((EnumDomainType) entityDomainType.getAttribute("kind").getType()).getEnumValues().size());
    }

    @Test
    public void testBuildUnionModel() {
        // Given
        DomainBuilder domainBuilder = createDefaultDomainBuilder();
        domainBuilder.createEntityType("Test")
            .addAttribute("name", "String", MetadataSample.INSTANCE)
            .withMetadata(MetadataSample.INSTANCE)
            .build();
        domainBuilder.createFunction("size")
            .withArgument("argument", "Collection|String")
            .withResultType("String")
            .build();
        // When
        DomainModel domainModel = domainBuilder.build();

        // Then
        EntityDomainType entityDomainType = (EntityDomainType) domainModel.getType("Test");
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getMetadata(MetadataSample.class));
        Assert.assertEquals(MetadataSample.INSTANCE, entityDomainType.getAttribute("name").getMetadata(MetadataSample.class));
        Assert.assertEquals("String", entityDomainType.getAttribute("name").getType().getName());

        DomainFunction sizeFunction = domainModel.getFunction("size");
        DomainType argumentType = sizeFunction.getArgument(0).getType();
        Assert.assertTrue(argumentType instanceof UnionDomainType);
        Assert.assertEquals("Collection|String", argumentType.getName());
        UnionDomainType unionDomainType = (UnionDomainType) argumentType;
        Assert.assertEquals("Collection", unionDomainType.getUnionElements().get(0).getName());
        Assert.assertEquals(DomainType.DomainTypeKind.COLLECTION, unionDomainType.getUnionElements().get(0).getKind());
        Assert.assertEquals("String", unionDomainType.getUnionElements().get(1).getName());
        Assert.assertEquals(DomainType.DomainTypeKind.BASIC, unionDomainType.getUnionElements().get(1).getKind());

        DomainModel newDomainModel = Domain.getDefaultProvider().createBuilder(domainModel)
            .extendBasicType("String", MetadataSample.INSTANCE)
            .build();

        assertNotSame(domainModel.getType("String"), newDomainModel.getType("String"));
        assertNotSame(domainModel.getType("Test"), newDomainModel.getType("Test"));
        assertNotSame(domainModel.getFunction("size"), newDomainModel.getFunction("size"));
    }

    @Test
    public void testBuildExtendedModelWithExplicitRemoved() {
        // Given
        DomainBuilder domainBuilder = createDefaultDomainBuilder();
        domainBuilder.createEntityType("Test")
            .addAttribute("name", "String", MetadataSample.INSTANCE)
            .withMetadata(MetadataSample.INSTANCE)
            .build();
        domainBuilder.createFunction("size")
            .withArgument("argument", "Collection|String")
            .withResultType("String")
            .build();
        DomainModel domainModel = domainBuilder.build();

        // When
        DomainBuilder builder = Domain.getDefaultProvider().createBuilder(domainModel);
        builder.removeType("Test");
        builder.removeFunction("size");
        DomainModel newDomainModel = builder.build();

        // Then
        assertNull(newDomainModel.getType("Test"));
        assertNull(newDomainModel.getFunction("size"));
    }

    private static class MetadataSample implements MetadataDefinition<MetadataSample> {

        public static final MetadataSample INSTANCE = new MetadataSample();

        @Override
        public Class<MetadataSample> getJavaType() {
            return MetadataSample.class;
        }

        @Override
        public MetadataSample build(MetadataDefinitionHolder definitionHolder) {
            return this;
        }
    }
}
