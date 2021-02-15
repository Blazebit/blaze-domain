/*
 * Copyright 2019 - 2021 Blazebit.
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
 * The domain operators that are available for domain types.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public enum DomainOperator {
    /**
     * The unary <code>+</code> operator.
     */
    UNARY_PLUS,
    /**
     * The unary <code>-</code> operator.
     */
    UNARY_MINUS,
    /**
     * The <code>+</code> operator.
     */
    PLUS,
    /**
     * The <code>-</code> operator.
     */
    MINUS,
    /**
     * The <code>*</code> operator.
     */
    MULTIPLICATION,
    /**
     * The <code>/</code> operator.
     */
    DIVISION,
    /**
     * The <code>%</code> operator.
     */
    MODULO,
    /**
     * The <code>!</code> operator.
     */
    NOT;

    /**
     * The arithmetic operators.
     */
    public static final Set<DomainOperator> ARITHMETIC;

    static {
        ARITHMETIC = Collections.unmodifiableSet(EnumSet.of(DomainOperator.PLUS, DomainOperator.MINUS, DomainOperator.MULTIPLICATION, DomainOperator.DIVISION, DomainOperator.MODULO, DomainOperator.UNARY_MINUS, DomainOperator.UNARY_PLUS));
    }

    /**
     * Returns the arithmetic operators.
     *
     * @return the arithmetic operators
     */
    public static DomainOperator[] arithmetic() {
        return ARITHMETIC.toArray(new DomainOperator[0]);
    }
}
