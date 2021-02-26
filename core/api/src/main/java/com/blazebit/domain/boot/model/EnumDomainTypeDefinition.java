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

package com.blazebit.domain.boot.model;

import java.util.Map;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface EnumDomainTypeDefinition extends DomainTypeDefinition {

    /**
     * The enum value of the enum domain type with the given name or <code>null</code>.
     *
     * @param name The name of the enum value
     * @return the enum value of the enum domain type
     */
    public EnumDomainTypeValueDefinition getEnumValue(String name);

    /**
     * The enum values of the enum domain type.
     *
     * @return the enum values of the enum domain type
     */
    public Map<String, ? extends EnumDomainTypeValueDefinition> getEnumValues();

}
