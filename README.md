[![Build Status](https://travis-ci.org/Blazebit/blaze-domain.svg?branch=master)](https://travis-ci.org/Blazebit/blaze-domain)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.blazebit/blaze-domain-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.blazebit/blaze-domain-api)
[![Slack Status](https://blazebit.herokuapp.com/badge.svg)](https://blazebit.herokuapp.com)

[![Javadoc - Domain](https://www.javadoc.io/badge/com.blazebit/blaze-domain-api.svg?label=javadoc%20-%20domain-api)](http://www.javadoc.io/doc/com.blazebit/blaze-domain-api)

Blaze-Domain
==========
Blaze-Domain is a toolkit that can be used to build a runtime model of a domain as a set of entities with attributes, basic types, functions and operators as well as metadata.

What is it?
===========

Blaze-Domain provides a builder API and runtime model to describe a domain in an extendible way. 

The domain description does not require that Java classes or methods/fields exist for domain entity types and their attributes or functions.
The _declarative_ submodule allows to determine a domain model based on class structures and their annotations which can be combined with the builder API to combine static and dynamic domain models.
The Blaze-Expression project builds on top of this project which allows to create an expression/predicate DSL based on a domain model defined via Blaze-Domain.
In the end, this allows to model expressions or predicates with a simplified DSL and custom domain model that can be transformed into JPQL.Next expressions to be consumed via Blaze-Persistence.

Features
==============

Blaze-Domain has support for

* Definition of structured types(Entity) with attributes
* Definition of custom functions
* Definition of custom basic types
* Definition of enumeration types
* Definition of collection types
* Configuration of enabled arithmetic operators and predicates per type
* Extendible metadata for every domain element
* Metadata extension for JPA related models
* Declarative definition of domain models

How to use it?
==============

WARNING: Blaze-Domain is still under heavy initial development and is not yet intended to be used!

Blaze-Domain is split up into different modules. We recommend that you define a version property in your parent pom that you can use for all artifacts. Modules are all released in one batch so you can safely increment just that property. 

```xml
<properties>
    <blaze-domain.version>1.0.0-SNAPSHOT</blaze-domain.version>
</properties>
```

Alternatively you can also use our BOM in the `dependencyManagement` section.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.blazebit</groupId>
            <artifactId>blaze-domain-bom</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>    
    </dependencies>
</dependencyManagement>
```