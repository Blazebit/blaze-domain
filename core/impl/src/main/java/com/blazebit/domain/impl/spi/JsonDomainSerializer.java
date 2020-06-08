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
import com.blazebit.domain.runtime.model.DomainPredicate;
import com.blazebit.domain.runtime.model.DomainType;
import com.blazebit.domain.runtime.model.EntityDomainType;
import com.blazebit.domain.runtime.model.EntityDomainTypeAttribute;
import com.blazebit.domain.runtime.model.EnumDomainType;
import com.blazebit.domain.runtime.model.EnumDomainTypeValue;
import com.blazebit.domain.spi.DomainSerializer;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * A JSON domain serializer.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class JsonDomainSerializer implements DomainSerializer<DomainModel> {

    @Override
    public <T> T serialize(DomainModel domainModel, DomainModel model, Class<T> targetType, String format, Map<String, Object> properties) {
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
                    serializeEntityDomainType(sb, (EntityDomainType) domainType, model, properties);
                    sb.append(',');
                } else if (domainType instanceof EnumDomainType) {
                    serializeEnumDomainType(sb, (EnumDomainType) domainType, model, properties);
                    sb.append(',');
                } else if (domainType instanceof BasicDomainType) {
                    serializeBasicDomainType(sb, (BasicDomainType) domainType, model, properties);
                    sb.append(',');
                }
            }
            for (CollectionDomainType collectionDomainType : collectionTypes.values()) {
                serializeCollectionDomainType(sb, collectionDomainType, model, properties);
                sb.append(',');
            }

            sb.setCharAt(sb.length() - 1, ']');
        }

        if (!model.getFunctions().isEmpty()) {
            sb.append(',');
            sb.append("\"funcs\": [");
            for (DomainFunction domainFunction : model.getFunctions().values()) {
                serializeFunction(sb, domainFunction, model, properties);
                sb.append(',');
            }
            sb.setCharAt(sb.length() - 1, ']');
        }

        serializerResolver("booleanLiteralResolver", model, model.getBooleanLiteralResolver(), properties, sb);
        serializerResolver("numericLiteralResolver", model, model.getNumericLiteralResolver(), properties, sb);
        serializerResolver("stringLiteralResolver", model, model.getStringLiteralResolver(), properties, sb);
        serializerResolver("temporalLiteralResolver", model, model.getTemporalLiteralResolver(), properties, sb);
        serializerResolver("entityLiteralResolver", model, model.getEntityLiteralResolver(), properties, sb);
        serializerResolver("enumLiteralResolver", model, model.getEnumLiteralResolver(), properties, sb);
        serializerResolver("collectionLiteralResolver", model, model.getCollectionLiteralResolver(), properties, sb);

        Map<String, Map<String, Set<DomainOperator>>> opResolvers = prepareResolvers(model, properties, DomainOperator.class, model.getOperationTypeResolvers());
        if (!opResolvers.isEmpty()) {
            sb.append(",\"opResolvers\": [");
            for (Map.Entry<String, Map<String, Set<DomainOperator>>> entry : opResolvers.entrySet()) {
                sb.append("{\"resolver\":");
                sb.append(entry.getKey());
                sb.append(",\"typeOps\":{");
                for (Map.Entry<String, Set<DomainOperator>> typeEntry : entry.getValue().entrySet()) {
                    sb.append("\"").append(typeEntry.getKey()).append("\":");
                    serializeDomainOperators(sb, typeEntry.getValue());
                    sb.append(',');
                }

                sb.setCharAt(sb.length() - 1, '}');
                sb.append("},");
            }

            sb.setCharAt(sb.length() - 1, ']');
        }

        Map<String, Map<String, Set<DomainPredicate>>> predResolvers = prepareResolvers(model, properties, DomainPredicate.class, model.getPredicateTypeResolvers());
        if (!predResolvers.isEmpty()) {
            sb.append(",\"predResolvers\": [");
            for (Map.Entry<String, Map<String, Set<DomainPredicate>>> entry : predResolvers.entrySet()) {
                sb.append("{\"resolver\":");
                sb.append(entry.getKey());
                if (predResolvers.size() > 1 || !entry.getKey().startsWith("{\"FixedDomainPredicateTypeResolver\":")) {
                    sb.append(",\"typePreds\":{");
                    for (Map.Entry<String, Set<DomainPredicate>> typeEntry : entry.getValue().entrySet()) {
                        sb.append("\"").append(typeEntry.getKey()).append("\":");
                        serializeDomainPredicates(sb, typeEntry.getValue());
                        sb.append(',');
                    }

                    sb.setCharAt(sb.length() - 1, '}');
                }
                sb.append("},");
            }

            sb.setCharAt(sb.length() - 1, ']');
        }

        sb.append('}');
        return (T) sb.toString();
    }

    private <D extends Enum<D>, R> Map<String, Map<String, Set<D>>> prepareResolvers(DomainModel model, Map<String, Object> properties, Class<D> type, Map<String, Map<D, R>> resolvers) {
        if (!model.getOperationTypeResolvers().isEmpty()) {
            Map<R, String> cachedSerializations = new IdentityHashMap<>();
            Map<String, Map<String, Set<D>>> resolverMap = new HashMap<>();
            StringBuilder tempSb = new StringBuilder();
            for (Map.Entry<String, Map<D, R>> typeEntry : resolvers.entrySet()) {
                for (Map.Entry<D, R> entry : typeEntry.getValue().entrySet()) {
                    String serialization = cachedSerializations.get(entry.getValue());
                    if (serialization == null) {
                        tempSb.setLength(0);
                        serializerResolver(model, entry.getValue(), properties, tempSb);
                        cachedSerializations.put(entry.getValue(), serialization = tempSb.toString());
                    }
                    if (!serialization.isEmpty()) {
                        resolverMap.computeIfAbsent(serialization, k -> new HashMap<>())
                            .computeIfAbsent(typeEntry.getKey(), k -> EnumSet.noneOf(type))
                            .add(entry.getKey());
                    }
                }
            }
            return resolverMap;
        }
        return Collections.emptyMap();
    }

    private void serializerResolver(DomainModel domainModel, Object resolver, Map<String, Object> properties, StringBuilder sb) {
        if (resolver instanceof DomainSerializer<?>) {
            String json = ((DomainSerializer<Object>) resolver).serialize(domainModel, null, String.class, "json", properties);
            if (json != null) {
                sb.append(json);
            }
        }
    }

    private void serializerResolver(String key, DomainModel domainModel, Object resolver, Map<String, Object> properties, StringBuilder sb) {
        if (resolver instanceof DomainSerializer<?>) {
            String json = ((DomainSerializer<Object>) resolver).serialize(domainModel, null, String.class, "json", properties);
            if (json != null) {
                sb.append(",\"").append(key).append("\":").append(json);
            }
        }
    }

    protected void serializeEntityDomainType(StringBuilder sb, EntityDomainType entityDomainType, DomainModel model, Map<String, Object> properties) {
        serializeDomainType(sb, entityDomainType, model, properties);

        sb.append(",\"attrs\":[");
        for (EntityDomainTypeAttribute attribute : entityDomainType.getAttributes().values()) {
            sb.append("{\"name\": \"").append(attribute.getName()).append("\",\"type\":\"").append(attribute.getType().getName()).append('"');
            serializeMetadata(sb, attribute.getMetadata(), model, properties);
            sb.append("},");
        }
        sb.setCharAt(sb.length() - 1, ']');
        sb.append('}');
    }

    protected void serializeEnumDomainType(StringBuilder sb, EnumDomainType enumDomainType, DomainModel model, Map<String, Object> properties) {
        serializeDomainType(sb, enumDomainType, model, properties);

        sb.append(",\"vals\":[");
        for (EnumDomainTypeValue value : enumDomainType.getEnumValues().values()) {
            sb.append("{\"name\": \"").append(value.getValue()).append('"');
            serializeMetadata(sb, value.getMetadata(), model, properties);
            sb.append("},");
        }
        sb.setCharAt(sb.length() - 1, ']');

        sb.append('}');
    }

    protected void serializeBasicDomainType(StringBuilder sb, BasicDomainType basicDomainType, DomainModel model, Map<String, Object> properties) {
        serializeDomainType(sb, basicDomainType, model, properties);
        sb.append('}');
    }

    protected void serializeCollectionDomainType(StringBuilder sb, CollectionDomainType collectionDomainType, DomainModel model, Map<String, Object> properties) {
        serializeDomainType(sb, collectionDomainType, model, properties);
        sb.append('}');
    }

    protected void serializeDomainType(StringBuilder sb, DomainType domainType, DomainModel model, Map<String, Object> properties) {
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
        sb.append('"');
        if (!domainType.getEnabledOperators().isEmpty()) {
            sb.append(",\"ops\":");
            serializeDomainOperators(sb, domainType.getEnabledOperators());
        }
        if (!domainType.getEnabledPredicates().isEmpty()) {
            sb.append(",\"preds\":");
            serializeDomainPredicates(sb, domainType.getEnabledPredicates());
        }

        serializeMetadata(sb, domainType.getMetadata(), model, properties);
    }

    protected void serializeDomainOperators(StringBuilder sb, Set<DomainOperator> operators) {
        sb.append("[");
        for (DomainOperator op : operators) {
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

    protected void serializeDomainPredicates(StringBuilder sb, Set<DomainPredicate> predicates) {
        sb.append("[");
        for (DomainPredicate pred : predicates) {
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

    protected void serializeFunction(StringBuilder sb, DomainFunction domainFunction, DomainModel model, Map<String, Object> properties) {
        sb.append("{\"name\":\"").append(domainFunction.getName())
            .append("\",\"argCount\":").append(domainFunction.getArgumentCount())
            .append(",\"minArgCount\":").append(domainFunction.getMinArgumentCount());
        if (domainFunction.getResultType() == null) {
            serializerResolver("typeResolver", model, model.getFunctionTypeResolver(domainFunction.getName()), properties, sb);
        } else {
            sb.append(",\"type\":\"").append(domainFunction.getResultType().getName()).append('"');
        }
        sb.append(",\"args\":[");

        if (domainFunction.getArguments().isEmpty()) {
            sb.append(']');
        } else {
            for (DomainFunctionArgument argument : domainFunction.getArguments()) {
                sb.append("{\"name\": \"").append(argument.getName()).append('"');
                if (argument.getType() != null) {
                    sb.append(",\"type\":\"").append(argument.getType().getName()).append('"');
                }
                serializeMetadata(sb, argument.getMetadata(), model, properties);
                sb.append("},");
            }
            sb.setCharAt(sb.length() - 1, ']');
        }
        serializeMetadata(sb, domainFunction.getMetadata(), model, properties);
        sb.append('}');
    }

    protected void serializeMetadata(StringBuilder sb, Map<Class<?>, Object> metadata, DomainModel model, Map<String, Object> properties) {
        if (!metadata.isEmpty()) {
            int start = sb.length();
            sb.append(",\"meta\":[");
            int beginIdx = sb.length();
            for (Object value : metadata.values()) {
                if (value instanceof DomainSerializer<?>) {
                    String result = ((DomainSerializer<Object>) value).serialize(model, value, String.class, "json", properties);
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
