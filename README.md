[![Build Status](https://travis-ci.com/Blazebit/blaze-domain.svg?branch=master)](https://travis-ci.org/Blazebit/blaze-domain)

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.blazebit/blaze-domain-core-api/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.blazebit/blaze-domain-core-api)
[![Slack Status](https://blazebit.herokuapp.com/badge.svg)](https://blazebit.herokuapp.com)

[![Javadoc - Domain](https://www.javadoc.io/badge/com.blazebit/blaze-domain-api.svg?label=javadoc%20-%20domain-core-api)](http://www.javadoc.io/doc/com.blazebit/blaze-domain-core-api)

Blaze-Domain
==========
Blaze-Domain is a toolkit that can be used to build a runtime model of a domain as a set of entities with attributes, basic types, functions and operators as well as metadata.

What is it?
===========

Blaze-Domain provides a builder API and runtime model to describe a domain in an extensible way. 

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
* Extensible metadata for every domain element
* Metadata extension for JPA related models
* Declarative definition of domain models
* TypeScript implementation for the client side validation of models

How to use it?
==============

Blaze-Domain is split up into different modules. We recommend that you define a version property in your parent pom that you can use for all artifacts. Modules are all released in one batch so you can safely increment just that property. 

```xml
<properties>
    <blaze-domain.version>1.0.9</blaze-domain.version>
</properties>
```

Alternatively you can also use our BOM in the `dependencyManagement` section.

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.blazebit</groupId>
            <artifactId>blaze-domain-bom</artifactId>
            <version>${blaze-domain.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>    
    </dependencies>
</dependencyManagement>
```

## Manual setup

For compiling you will only need API artifacts and for the runtime you need impl and integration artifacts.

Blaze-Domain Core module dependencies

```xml
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-domain-core-api</artifactId>
    <version>${blaze-domain.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-domain-core-impl</artifactId>
    <version>${blaze-domain.version}</version>
    <scope>runtime</scope>
</dependency>
```

Blaze-Domain Declarative module dependencies

```xml
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-domain-declarative-api</artifactId>
    <version>${blaze-domain.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-domain-declarative-impl</artifactId>
    <version>${blaze-domain.version}</version>
    <scope>runtime</scope>
</dependency>
```

Blaze-Domain Declarative CDI integration dependencies

```xml
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-domain-declarative-integration-cdi</artifactId>
    <version>${blaze-domain.version}</version>
    <scope>runtime</scope>
</dependency>
```

Blaze-Domain Persistence module dependencies

```xml
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-domain-persistence</artifactId>
    <version>${blaze-domain.version}</version>
    <scope>compile</scope>
</dependency>
<dependency>
    <groupId>com.blazebit</groupId>
    <artifactId>blaze-domain-declarative-persistence</artifactId>
    <version>${blaze-domain.version}</version>
    <scope>compile</scope>
</dependency>
```

Documentation
=========

Currently there is no documentation other than the Javadoc.
 
Core quick-start
=================

Building a domain model works through the `DomainBuilder` API. 

```java
DomainBuilder domainBuilder = Domain.getDefaultProvider().createDefaultBuilder();
domainBuilder.createEntityType("Cat")
    .addAttribute("name", String.class)
    .addAttribute("age", Integer.class)
  .build();
DomainModel domain = domainBuilder.build();
```

This will build an entity type with the domain type name `Cat` containing two attributes `name` and `age`.
The domain model can then be queried.

```java
// Returns a basic domain type for the java type String
domain.getEntityType("Cat").getAttribute("name").getType();
```

This alone is not very spectacular, but the declarative module allows to interpret class structures as domain types which saves a lot of typing and is safer.

Declarative usage
=================

The declarative module allows to define domain models through java class definitions:

```java
@DomainType
interface Cat {
  String getName();
  Integer getAge();
}
```

which can then be registered like this:

```java
DeclarativeDomainConfiguration config = DeclarativeDomain.getDefaultProvider().createDefaultConfiguration();
config.addDomainType(Cat.class);
DomainModel domain = config.createDomainModel();
```

The discovery and registering can be automated by making use of the CDI integration `blaze-domain-declarative-integration-cdi`.

Licensing
=========

This distribution, as a whole, is licensed under the terms of the Apache
License, Version 2.0 (see LICENSE.txt).

References
==========

Project Site:              https://domain.blazebit.com (coming at some point)
