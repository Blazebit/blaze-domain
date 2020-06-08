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

package com.blazebit.domain.runtime.model;

import java.util.Set;

/**
 * A type in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainType extends MetadataHolder {

    /**
     * The name of the domain type.
     *
     * @return the name of the domain type
     */
    public String getName();

    /**
     * The Java type of the domain type or <code>null</code>.
     *
     * @return the java type or <code>null</code>
     */
    public Class<?> getJavaType();

    /**
     * The domain type kind.
     *
     * @return the domain type kind
     */
    public DomainTypeKind getKind();

    /**
     * The domain operators that are enabled for this domain type.
     *
     * @return the enabled domain operators
     */
    public Set<DomainOperator> getEnabledOperators();

    /**
     * The domain predicates that are enabled for this domain type.
     *
     * @return the enabled domain predicates
     */
    public Set<DomainPredicate> getEnabledPredicates();

    /**
     * The domain type kinds.
     *
     * @author Christian Beikov
     * @since 1.0.0
     */
    public static enum DomainTypeKind {
        /**
         * A basic domain type.
         */
        BASIC,
        /**
         * An enum domain type.
         */
        ENUM,
        /**
         * An entity domain type.
         */
        ENTITY,
        /**
         * A collection domain type.
         */
        COLLECTION;
    }

}
