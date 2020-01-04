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

package com.blazebit.domain.declarative.spi;

import java.util.Map;

/**
 * @param <B> The builder type
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface ServiceProvider<B> {

    /**
     * Returns the registered service for the given type.
     *
     * @param serviceClass The service class
     * @param <T> The service type
     * @return the registered service
     */
    <T> T getService(Class<T> serviceClass);

    /**
     * Returns the registered services.
     *
     * @return the registered services
     */
    Map<Class<?>, Object> getServices();

    /**
     * Registers the given service for the given type.
     *
     * @param serviceClass The service class
     * @param service The service
     * @param <T> The service type
     * @return this for chaining
     */
    <T> B withService(Class<T> serviceClass, T service);
}
