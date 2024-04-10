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

package com.blazebit.domain.runtime.model;

import java.util.Map;

/**
 * An entity type in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface EntityDomainType extends DomainType {

    /**
     * Returns the attribute with the given name or <code>null</code>.
     *
     * @param name The name of the desired attribute
     * @return the attribute or <code>null</code>
     */
    public EntityDomainTypeAttribute getAttribute(String name);

    /**
     * Returns the attributes of the entity domain type.
     *
     * @return the attributes
     */
    public Map<String, ? extends EntityDomainTypeAttribute> getAttributes();

}
