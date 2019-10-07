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

package com.blazebit.domain.runtime.model;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/**
 * The domain predicates that are available for domain types.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public enum DomainPredicateType {
    /**
     * The nullness predicates <code>IS NULL</code>/<code>IS NOT NULL</code>.
     */
    NULLNESS,
    /**
     * The collection predicates <code>IS EMPTY</code>/<code>IS NOT EMPTY</code>.
     */
    COLLECTION,
    /**
     * The relational predicates <code>&lt;</code>/<code>&lt;=</code>/<code>&gt;</code>/<code>&gt;=</code>.
     */
    RELATIONAL,
    /**
     * The equality predicates <code>=</code>/<code>!=</code>/<code>&lt;&gt;</code>.
     */
    EQUALITY;

    /**
     * The comparable predicates.
     */
    public static final Set<DomainPredicateType> COMPARABLE;
    /**
     * The distinguishable predicates.
     */
    public static final Set<DomainPredicateType> DISTINGUISHABLE;

    static {
        COMPARABLE = Collections.unmodifiableSet(EnumSet.of(DomainPredicateType.RELATIONAL, DomainPredicateType.EQUALITY, DomainPredicateType.NULLNESS));
        DISTINGUISHABLE = Collections.unmodifiableSet(EnumSet.of(DomainPredicateType.EQUALITY, DomainPredicateType.NULLNESS));
    }

    /**
     * Returns the comparable predicates.
     *
     * @return the comparable predicates
     */
    public static DomainPredicateType[] comparable() {
        return COMPARABLE.toArray(new DomainPredicateType[0]);
    }

    /**
     * Returns the distinguishable predicates.
     *
     * @return the distinguishable predicates
     */
    public static DomainPredicateType[] distinguishable() {
        return DISTINGUISHABLE.toArray(new DomainPredicateType[0]);
    }
}
