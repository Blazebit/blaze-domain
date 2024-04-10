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

package com.blazebit.domain.declarative;

/**
 * The discovery mode for declarative domain elements.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public enum DiscoverMode {

    /**
     * The explicit mode requires domain elements to be annotated explicitly.
     */
    EXPLICIT,
    /**
     * The implicit mode will interpret all type members as domain elements except those annotated with {@link Transient}.
     */
    IMPLICIT,
    /**
     * The automatic mode will behave like the {@link #IMPLICIT} for now.
     */
    AUTO;

}
