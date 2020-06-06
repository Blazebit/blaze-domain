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

package com.blazebit.domain.impl.spi;

import com.blazebit.domain.runtime.model.BasicDomainType;
import com.blazebit.domain.runtime.model.CollectionDomainType;
import com.blazebit.domain.runtime.model.DomainFunction;
import com.blazebit.domain.runtime.model.DomainFunctionArgument;
import com.blazebit.domain.runtime.model.DomainModel;
import com.blazebit.domain.runtime.model.DomainOperator;
import com.blazebit.domain.runtime.model.DomainPredicateType;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.EntityDomainType;
import com.blazebit.domain.runtime.model.EntityDomainTypeAttribute;
import com.blazebit.domain.runtime.model.EnumDomainType;
import com.blazebit.domain.runtime.model.EnumDomainTypeValue;
import com.blazebit.domain.spi.DomainSerializer;

import java.util.Map;

/**
 * A JSON domain serializer.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class JsonDomainSerializer implements DomainSerializer<DomainModel> {

    @Override
    public <T> T serialize(DomainModel model, Class<T> targetType, String format) {
        if (targetType != String.class || !"json".equals(format)) {
            return null;
        }
        Map<String, DomainType> types = model.getTypes();
        Map<DomainType, CollectionDomainType> collectionTypes = model.getCollectionTypes();
        StringBuilder sb = new StringBuilder();
        sb.append('{');

        sb.append("\"types\": [");
        if (types.isEmpty() && collectionTypes.isEmpty()) {
            sb.append(']');
        } else {
            for (DomainType domainType : types.values()) {
                if (domainType instanceof EntityDomainType) {
                    serializeEntityDomainType(sb, (EntityDomainType) domainType);
                } else if (domainType instanceof EnumDomainType) {
                    serializeEnumDomainType(sb, (EnumDomainType) domainType);
                } else {
                    serializeBasicDomainType(sb, (BasicDomainType) domainType);
                }
                sb.append(',');
            }
            for (CollectionDomainType collectionDomainType : collectionTypes.values()) {
                serializeCollectionDomainType(sb, collectionDomainType);
                sb.append(',');
            }

            sb.setCharAt(sb.length() - 1, ']');
        }

        if (!model.getFunctions().isEmpty()) {
            sb.append(',');
            sb.append("\"funcs\": [");
            for (DomainFunction domainFunction : model.getFunctions().values()) {
                serializeFunction(sb, domainFunction);
                sb.append(',');
            }
            sb.setCharAt(sb.length() - 1, ']');
        }

        sb.append('}');
        return (T) sb.toString();
    }

    protected void serializeEntityDomainType(StringBuilder sb, EntityDomainType entityDomainType) {
        serializeDomainType(sb, entityDomainType);

        sb.append("\",\"attrs\":[");
        for (EntityDomainTypeAttribute attribute : entityDomainType.getAttributes().values()) {
            sb.append("{\"name\": \"").append(attribute.getName()).append("\",\"type\":\"").append(attribute.getType().getName()).append('"');
            serializeMetadata(sb, attribute.getMetadata());
            sb.append("},");
        }
        sb.setCharAt(sb.length() - 1, ']');
        sb.append('}');
    }

    protected void serializeEnumDomainType(StringBuilder sb, EnumDomainType enumDomainType) {
        serializeDomainType(sb, enumDomainType);

        sb.append("\",\"vals\":[");
        for (EnumDomainTypeValue value : enumDomainType.getEnumValues().values()) {
            sb.append("{\"name\": \"").append(value.getValue()).append('"');
            serializeMetadata(sb, value.getMetadata());
            sb.append("},");
        }
        sb.setCharAt(sb.length() - 1, ']');

        sb.append('}');
    }

    protected void serializeBasicDomainType(StringBuilder sb, BasicDomainType basicDomainType) {
        serializeDomainType(sb, basicDomainType);
        sb.append('}');
    }

    protected void serializeCollectionDomainType(StringBuilder sb, CollectionDomainType collectionDomainType) {
        serializeDomainType(sb, collectionDomainType);
        sb.append('}');
    }

    protected void serializeDomainType(StringBuilder sb, DomainType domainType) {
        sb.append("{\"name\":\"").append(domainType.getName()).append("\",\"kind\":\"");
        switch (domainType.getKind()) {
            case BASIC:
                sb.append('B');
                break;
            case ENTITY:
                sb.append('E');
                break;
            case ENUM:
                sb.append('N');
                break;
            case COLLECTION:
                sb.append('C');
                break;
            default:
                throw new IllegalArgumentException("Unsupported domain type kind: " + domainType.getKind());
        }
        if (!domainType.getEnabledOperators().isEmpty()) {
            sb.append("\",\"ops\":[");
            for (DomainOperator op : domainType.getEnabledOperators()) {
                sb.append('"');
                switch (op) {
                    case UNARY_MINUS:
                        sb.append('M');
                        break;
                    case UNARY_PLUS:
                        sb.append('P');
                        break;
                    case DIVISION:
                        sb.append('/');
                        break;
                    case MINUS:
                        sb.append('-');
                        break;
                    case MODULO:
                        sb.append('%');
                        break;
                    case MULTIPLICATION:
                        sb.append('*');
                        break;
                    case NOT:
                        sb.append('!');
                        break;
                    case PLUS:
                        sb.append('+');
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported domain operator: " + op);
                }
                sb.append("\",");
            }
            sb.setCharAt(sb.length() - 1, ']');
        }
        if (!domainType.getEnabledPredicates().isEmpty()) {
            sb.append("\",\"preds\":[");
            for (DomainPredicateType pred : domainType.getEnabledPredicates()) {
                sb.append('"');
                switch (pred) {
                    case COLLECTION:
                        sb.append('C');
                        break;
                    case EQUALITY:
                        sb.append('E');
                        break;
                    case NULLNESS:
                        sb.append('N');
                        break;
                    case RELATIONAL:
                        sb.append('R');
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported domain predicate: " + pred);
                }
                sb.append("\",");
            }
            sb.setCharAt(sb.length() - 1, ']');
        }

        serializeMetadata(sb, domainType.getMetadata());
    }

    protected void serializeFunction(StringBuilder sb, DomainFunction domainFunction) {
        sb.append("{\"name\":\"").append(domainFunction.getName())
            .append("\",\"argCount\":").append(domainFunction.getArgumentCount())
            .append(",\"minArgCount\":").append(domainFunction.getMinArgumentCount())
            .append(",\"type\":\"").append(domainFunction.getResultType().getName())
            .append("\",\"args\":[");

        if (domainFunction.getArguments().isEmpty()) {
            sb.append(']');
        } else {
            for (DomainFunctionArgument argument : domainFunction.getArguments()) {
                sb.append("{\"name\": \"").append(argument.getName()).append("\",\"type\":\"").append(argument.getType().getName()).append('"');
                serializeMetadata(sb, argument.getMetadata());
                sb.append("},");
            }
            sb.setCharAt(sb.length() - 1, ']');
        }
        serializeMetadata(sb, domainFunction.getMetadata());
    }

    protected void serializeMetadata(StringBuilder sb, Map<Class<?>, Object> metadata) {
        if (!metadata.isEmpty()) {
            int start = sb.length();
            sb.append("\",\"meta\":[");
            int beginIdx = sb.length();
            for (Object value : metadata.values()) {
                if (value instanceof DomainSerializer) {
                    String result = ((DomainSerializer<Object>) value).serialize(value, String.class, "json");
                    if (result != null) {
                        sb.append(result).append(',');
                    }
                }
            }
            if (beginIdx == sb.length()) {
                sb.setLength(start);
            } else {
                sb.setCharAt(sb.length() - 1, ']');
            }
        }
    }

}
