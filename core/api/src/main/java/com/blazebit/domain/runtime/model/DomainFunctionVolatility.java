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

package com.blazebit.domain.runtime.model;

/**
 * The volatility of a domain function.
 *
 * @author Christian Beikov
 * @since 2.0.3
 */
public enum DomainFunctionVolatility {
    /**
     * The function result can change at any time and is not just dependent on the arguments.
     */
    VOLATILE,
    /**
     * The function result depends on the arguments and possibly other state which stays stable throughout the execution.
     */
    STABLE,
    /**
     * The function result depends just on the arguments.
     */
    IMMUTABLE;
}
