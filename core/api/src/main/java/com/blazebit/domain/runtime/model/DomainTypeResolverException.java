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
 * A domain type resolver exception.
 *
 * @author Christian Beikov
 * @since 1.0.4
 */
public class DomainTypeResolverException extends RuntimeException {

    /**
     * Creates a new exception.
     */
    public DomainTypeResolverException() {
    }

    /**
     * Creates a new exception.
     *
     * @param message The message
     */
    public DomainTypeResolverException(String message) {
        super(message);
    }

    /**
     * Creates a new exception.
     *
     * @param message The message
     * @param cause The cause
     */
    public DomainTypeResolverException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new exception.
     *
     * @param cause The cause
     */
    public DomainTypeResolverException(Throwable cause) {
        super(cause);
    }

    /**
     * Creates a new exception.
     *
     * @param message The message
     * @param cause The cause
     * @param enableSuppression Whether to enable suppression
     * @param writableStackTrace Whether the stack trace should be writable
     */
    public DomainTypeResolverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
