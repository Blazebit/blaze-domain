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

package com.blazebit.domain.impl.boot.model;

import com.blazebit.domain.boot.model.DomainFunctionArgumentDefinition;
import com.blazebit.domain.boot.model.DomainTypeDefinition;
import com.blazebit.domain.impl.runtime.model.DomainFunctionArgumentImpl;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;

/**
 * @author Christian Beikov
 * @since 1.0.0
 */
public interface DomainFunctionArgumentDefinitionImplementor extends DomainFunctionArgumentDefinition {

    public DomainTypeDefinition getTypeDefinition();

    public DomainFunctionArgument getDomainFunctionArgument();

    public DomainFunctionArgumentImpl createFunctionArgument(DomainFunction function, MetamodelBuildingContext context);
}
