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

/**
 * A map with string keys.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
interface StringMap<T> {
    [key: string]: T;
}

/**
 * The domain type kinds.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export enum DomainTypeKind {
    /**
     * A basic domain type.
     */
    BASIC,
    /**
     * An enum domain type.
     */
    ENUM,
    /**
     * An entity domain type.
     */
    ENTITY,
    /**
     * A collection domain type.
     */
    COLLECTION
}

/**
 * The domain operators that are available for domain types.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export enum DomainOperator {
    /**
     * The unary <code>+</code> operator.
     */
    UNARY_PLUS,
    /**
     * The unary <code>-</code> operator.
     */
    UNARY_MINUS,
    /**
     * The <code>+</code> operator.
     */
    PLUS,
    /**
     * The <code>-</code> operator.
     */
    MINUS,
    /**
     * The <code>*</code> operator.
     */
    MULTIPLICATION,
    /**
     * The <code>/</code> operator.
     */
    DIVISION,
    /**
     * The <code>%</code> operator.
     */
    MODULO,
    /**
     * The <code>!</code> operator.
     */
    NOT
}

/**
 * The domain predicates that are available for domain types.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export enum DomainPredicateType {
    /**
     * The nullness predicates <code>IS NULL</code>/<code>IS NOT NULL</code>.
     */
    NULLNESS,
    /**
     * The collection predicates <code>IS EMPTY</code>/<code>IS NOT EMPTY</code>.
     */
    COLLECTION,
    /**
     * The relational predicates <code>&lt;</code>/<code>&lt;=</code>/<code>&gt;</code>/<code>&gt;=</code>.
     */
    RELATIONAL,
    /**
     * The equality predicates <code>=</code>/<code>!=</code>/<code>&lt;&gt;</code>.
     */
    EQUALITY
}

/**
 * A domain element that can have metadata.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export abstract class MetadataHolder {
    /**
     * The metadata objects.
     */
    metadata: any[];

    protected constructor(metadata: any[]) {
        this.metadata = metadata;
    }
}

/**
 * A type in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export abstract class DomainType extends MetadataHolder {
    /**
     * The name of the domain type.
     */
    name: string;
    /**
     * The domain type kind.
     */
    kind: DomainTypeKind;
    /**
     * The domain operators that are enabled for this domain type.
     */
    enabledOperators: readonly DomainOperator[];
    /**
     * The domain predicates that are enabled for this domain type.
     */
    enabledPredicates: readonly DomainPredicateType[];

    constructor(name: string, kind: DomainTypeKind, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicateType[], metadata: any[]) {
        super(metadata);
        this.name = name;
        this.kind = kind;
        this.enabledOperators = enabledOperators;
        this.enabledPredicates = enabledPredicates;
    }
}

/**
 * A basic type in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class BasicDomainType extends DomainType {

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicateType[], metadata: any[]) {
        super(name, DomainTypeKind.BASIC, enabledOperators, enabledPredicates, metadata);
    }
}

/**
 * A collection type in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class CollectionDomainType extends DomainType {
    /**
     * The domain type of the collection element.
     */
    elementType: DomainType;

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicateType[], elementType: DomainType, metadata: any[]) {
        super(name, DomainTypeKind.COLLECTION, enabledOperators, enabledPredicates, metadata);
        this.elementType = elementType;
    }
}

/**
 * A function in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class DomainFunction extends MetadataHolder {
    /**
     * The name of the domain function.
     */
    name: string;
    /**
     * The minimum argument count for the function.
     */
    minArgumentCount: number;
    /**
     * The maximum argument count for the function.
     */
    argumentCount: number;
    /**
     * The domain function result type if fixed, otherwise <code>null</code>.
     */
    resultType: DomainType;
    /**
     * The function documentation.
     */
    documentation: string;
    /**
     * The domain function arguments.
     */
    arguments: readonly DomainFunctionArgument[];

    constructor(name: string, minArgumentCount: number, argumentCount: number, resultType: DomainType, documentation: string, args: readonly DomainFunctionArgument[], metadata: any[]) {
        super(metadata);
        this.name = name;
        this.minArgumentCount = minArgumentCount;
        this.argumentCount = argumentCount;
        this.resultType = resultType;
        this.documentation = documentation;
        this.arguments = args;
    }
}
/**
 * Represents the argument to a domain function.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class DomainFunctionArgument extends MetadataHolder {
    /**
     * The name of the function argument or <code>null</code>.
     */
    name: string;
    /**
     * The 0-based positional index of the function argument.
     */
    position: number;
    /**
     * The domain type of the function argument or <code>null</code>.
     */
    type: DomainType;
    /**
     * The function argument documentation.
     */
    documentation: string;

    constructor(name: string, position: number, type: DomainType, documentation: string, metadata: any[]) {
        super(metadata);
        this.name = name;
        this.position = position;
        this.type = type;
        this.documentation = documentation;
    }
}
/**
 * An entity type in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class EntityDomainType extends DomainType {
    /**
     * The attributes of the entity domain type.
     */
    attributes: StringMap<EntityAttribute>;

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicateType[], attributes: StringMap<EntityAttribute>, metadata: any[]) {
        super(name, DomainTypeKind.ENTITY, enabledOperators, enabledPredicates, metadata);
        this.attributes = attributes;
    }
}
/**
 * An entity attribute of an entity domain type.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class EntityAttribute extends MetadataHolder {
    /**
     * The name of the attribute.
     */
    name: string;
    /**
     * The domain type of the attribute.
     */
    type: DomainType;
    /**
     * The entity attribute documentation.
     */
    documentation: string;

    constructor(name: string, type: DomainType, documentation: string, metadata: any[]) {
        super(metadata);
        this.name = name;
        this.type = type;
        this.documentation = documentation;
    }
}
/**
 * An enum type in the domain.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class EnumDomainType extends DomainType {
    /**
     * The enum domain type values.
     *
     * @return the enum domain type values
     */
    enumValues: StringMap<EnumDomainTypeValue>;

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicateType[], enumValues: StringMap<EnumDomainTypeValue>, metadata: any[]) {
        super(name, DomainTypeKind.ENUM, enabledOperators, enabledPredicates, metadata);
        this.enumValues = enumValues;
    }
}
/**
 * An enum value of an enum domain type.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class EnumDomainTypeValue extends MetadataHolder {
    /**
     * The enum value.
     *
     * @return the enum value
     */
    value: string;
    /**
     * The enum value documentation.
     */
    documentation: string;

    constructor(value: string, documentation: string, metadata: any[]) {
        super(metadata);
        this.value = value;
        this.documentation = documentation;
    }
}

/**
 * A type checked domain model that can be used for domain introspection.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class DomainModel {
    /**
     * Returns the types of the domain model as map indexed by their type name.
     */
    types: StringMap<DomainType>;
    /**
     * Returns the functions of the domain model as map indexed by their function name.
     */
    functions: StringMap<DomainFunction>;

    /**
     * Parses the given JSON string to a domain model.
     *
     * @param input The JSON string
     */
    static parse(input: string): DomainModel {
        let parseMeta = function(m: any): any[] {
            if (Array.isArray(m)) {
                return m;
            } else {
                return [];
            }
        };
        let doc = function(meta: any[]): string {
            for (let m of meta) {
                if (m.hasOwnProperty('doc')) {
                    return m['doc'];
                }
            }
            return null;
        };
        let json = JSON.parse(input), types = json['types'], functions = json['funcs'];
        var domainTypes: StringMap<DomainType> = {};
        types.forEach(function (type) {
            let name = type['name'];
            let ops: DomainOperator[] = [];
            if (Array.isArray(type['ops'])) {
                type['ops'].forEach(function (op) {
                    switch (op) {
                        case 'M':
                            ops.push(DomainOperator.UNARY_MINUS);
                            break;
                        case 'P':
                            ops.push(DomainOperator.UNARY_PLUS);
                            break;
                        case '/':
                            ops.push(DomainOperator.DIVISION);
                            break;
                        case '-':
                            ops.push(DomainOperator.MINUS);
                            break;
                        case '%':
                            ops.push(DomainOperator.MODULO);
                            break;
                        case '*':
                            ops.push(DomainOperator.MULTIPLICATION);
                            break;
                        case '!':
                            ops.push(DomainOperator.NOT);
                            break;
                        case '+':
                            ops.push(DomainOperator.PLUS);
                            break;
                    }
                });
            }
            let preds: DomainPredicateType[] = [];
            if (Array.isArray(type['preds'])) {
                type['preds'].forEach(function (pred) {
                    switch (pred) {
                        case 'C':
                            preds.push(DomainPredicateType.COLLECTION);
                            break;
                        case 'E':
                            preds.push(DomainPredicateType.EQUALITY);
                            break;
                        case 'N':
                            preds.push(DomainPredicateType.NULLNESS);
                            break;
                        case 'R':
                            preds.push(DomainPredicateType.RELATIONAL);
                            break;
                    }
                });
            }
            let meta = parseMeta(type['meta']);
            switch (type['kind']) {
                case 'B':
                    domainTypes[name] = new BasicDomainType(name, ops, preds, meta);
                    break;
                case 'C':
                    domainTypes[name] = new CollectionDomainType(name, ops, preds, null, meta);
                    break;
                case 'E':
                    var attrs: StringMap<EntityAttribute> = {};
                    domainTypes[name] = new EntityDomainType(name, ops, preds, attrs, meta);
                    break;
                case 'N':
                    var vals: StringMap<EnumDomainTypeValue> = {};
                    type['vals'].forEach(function (val) {
                        let valMeta = parseMeta(val);
                        vals[val] = new EnumDomainTypeValue(val, doc(valMeta), valMeta);
                    });
                    domainTypes[name] = new EnumDomainType(name, ops, preds, vals, meta);
                    break;
            }
        });
        types.forEach(function (type) {
            let name = type['name'];
            if (type['kind'] == 'E') {
                let entityType = domainTypes[name] as EntityDomainType;
                if (Array.isArray(type['attrs'])) {
                    type['attrs'].forEach(function (a) {
                        let attrMeta = parseMeta(a['meta']);
                        entityType.attributes[a['name']] = new EntityAttribute(a['name'], domainTypes[a['type']], doc(attrMeta), attrMeta);
                    });
                }
            } else if (type['kind'] == 'C') {
                let collectionType = domainTypes[name] as CollectionDomainType;
                let prefix = 'Collection<';
                collectionType.elementType = domainTypes[name.substring(prefix.length + 1, name.length - 1)];
            }
        });
        var funcs: StringMap<DomainFunction> = {};
        if (Array.isArray(functions)) {
            functions.forEach(function (func) {
                var params: DomainFunctionArgument[] = [];
                let args = func['args'];
                if (Array.isArray(args)) {
                    for (var i = 0; i < args.length; i++) {
                        let param = args[i];
                        let paramMeta = parseMeta(param['meta']);
                        params.push(new DomainFunctionArgument(param['name'], i, domainTypes[param['type']], doc(paramMeta), paramMeta));
                    }
                }
                let meta = parseMeta(func['meta']);
                funcs[func['name']] = new DomainFunction(func['name'], func['minArgCount'], func['argCount'], domainTypes[func['type']], doc(meta), params, meta);
            });
        }

        return { types: domainTypes, functions: funcs };
    }
}