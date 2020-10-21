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

import java.util.Arrays;

/**
 * A cache key for a fixed return type with restricted argument types.
 *
 * @param <T> The array component type
 * @author Christian Beikov
 * @since 1.0.12
 */
class RestrictedCacheKey<T> {

    private final T returningType;
    private final T[] supportedTypes;

    /**
     * Creates a cache key for the given returning type and array.
     *
     * @param returningType The returning type
     * @param supportedTypes The array of supported types
     */
    RestrictedCacheKey(T returningType, T[] supportedTypes) {
        this.returningType = returningType;
        this.supportedTypes = supportedTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        RestrictedCacheKey<?> restrictedCacheKey = (RestrictedCacheKey<?>) o;
        return returningType.equals(restrictedCacheKey.returningType) && Arrays.equals(supportedTypes, restrictedCacheKey.supportedTypes);
    }

    @Override
    public int hashCode() {
        int result = returningType.hashCode();
        result = 31 * result + Arrays.hashCode(supportedTypes);
        return result;
    }
}
