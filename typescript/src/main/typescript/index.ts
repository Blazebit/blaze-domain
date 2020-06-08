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
export enum DomainPredicate {
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
    enabledPredicates: readonly DomainPredicate[];

    constructor(name: string, kind: DomainTypeKind, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicate[], metadata: any[]) {
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

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicate[], metadata: any[]) {
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

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicate[], elementType: DomainType, metadata: any[]) {
        super(name, DomainTypeKind.COLLECTION, enabledOperators, enabledPredicates, metadata);
        this.elementType = elementType;
    }
}

/**
 * A domain predicate type resolver.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export interface DomainPredicateTypeResolver {
    /**
     * Resolves the domain type for applying a predicate on the given operand domain type.
     *
     * @param domainModel The domain model
     * @param domainTypes The operand domain types
     * @return the resolved type
     */
    resolveType(domainModel: DomainModel, domainTypes: DomainType[]): DomainType;
}

/**
 * A domain operation type resolver.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export interface DomainOperationTypeResolver {
    /**
     * Resolves the domain type for applying an operator on the given operand domain type.
     *
     * @param domainModel The domain model
     * @param domainTypes The operand domain types
     * @return the resolved type
     */
    resolveType(domainModel: DomainModel, domainTypes: DomainType[]): DomainType;
}

/**
 * A domain function return type resolver.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export interface DomainFunctionTypeResolver {
    /**
     * Resolves the domain function return type for the given argument type assignments.
     *
     * @param domainModel The domain model
     * @param domainFunction The domain function
     * @param argumentTypes The domain function argument types
     * @return the resolved function return type
     */
    resolveType(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType;
}

/**
 * An entity literal.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export interface EntityLiteral {
    /**
     * The entity type.
     */
    entityType: EntityDomainType;
    /**
     * The entity attribute values.
     */
    attributeValues: StringMap<any>;
}

/**
 * A collection literal.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export interface CollectionLiteral {
    /**
     * The collection type.
     */
    type: CollectionDomainType;
    /**
     * The collection values.
     */
    values: any[];
}

/**
 * The literal kind.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export enum LiteralKind {
    /**
     * Boolean literal.
     */
    BOOLEAN,
    /**
     * Numeric literal.
     */
    NUMERIC,
    /**
     * String literal.
     */
    STRING,
    /**
     * Timestamp literal.
     */
    TIMESTAMP,
    /**
     * Interval literal.
     */
    INTERVAL,
    /**
     * Entity literal.
     */
    ENTITY,
    /**
     * Enum literal.
     */
    ENUM,
    /**
     * Collection literal.
     */
    COLLECTION
}

/**
 * A resolver for literal values.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export interface LiteralResolver {
    /**
     * Resolves the domain type of the given literal value.
     *
     * @param domainModel The domain model
     * @param kind The kind of literal
     * @param value The boolean value
     * @return the resolved literal
     */
    resolveLiteral(domainModel: DomainModel, kind: LiteralKind, value: boolean | string | EntityLiteral | CollectionLiteral): DomainType;
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
     * The domain function result type resolver if the return type is not fixed, otherwise <code>null</code>.
     */
    resultTypeResolver: DomainFunctionTypeResolver;
    /**
     * The function documentation.
     */
    documentation: string;
    /**
     * The domain function arguments.
     */
    arguments: readonly DomainFunctionArgument[];

    constructor(name: string, minArgumentCount: number, argumentCount: number, resultType: DomainType, resultTypeResolver: DomainFunctionTypeResolver, documentation: string, args: readonly DomainFunctionArgument[], metadata: any[]) {
        super(metadata);
        this.name = name;
        this.minArgumentCount = minArgumentCount;
        this.argumentCount = argumentCount;
        this.resultType = resultType;
        this.resultTypeResolver = resultTypeResolver;
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

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicate[], attributes: StringMap<EntityAttribute>, metadata: any[]) {
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

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicate[], enumValues: StringMap<EnumDomainTypeValue>, metadata: any[]) {
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
     * Returns the operation type resolvers of the domain model as map indexed by their type name.
     */
    operationTypeResolvers: StringMap<StringMap<DomainOperationTypeResolver>>;
    /**
     * Returns the predicate type resolvers of the domain model as map indexed by their type name.
     */
    predicateTypeResolvers: StringMap<StringMap<DomainPredicateTypeResolver>>;
    /**
     * Returns the resolver for boolean literals.
     */
    booleanLiteralResolver: LiteralResolver;
    /**
     * Returns the resolver for numeric literals.
     */
    numericLiteralResolver: LiteralResolver;
    /**
     * Returns the resolver for string literals.
     */
    stringLiteralResolver: LiteralResolver;
    /**
     * Returns the resolver for temporal literals.
     */
    temporalLiteralResolver: LiteralResolver;
    /**
     * Returns the resolver for entity literals.
     */
    entityLiteralResolver: LiteralResolver;
    /**
     * Returns the resolver for enum literals.
     */
    enumLiteralResolver: LiteralResolver;
    /**
     * Returns the resolver for collection literals.
     */
    collectionLiteralResolver: LiteralResolver;

    /**
     * Parses the given JSON string to a domain model.
     *
     * @param input The JSON string
     * @param extensions The extension functions like resolver constructors
     */
    static parse(input: string, extensions: StringMap<Function>): DomainModel {
        if (typeof extensions === "undefined") {
            extensions = {};
        }
        let registerIfAbsent = function(k: string, f: Function) {
            if (!(extensions[k] instanceof Function)) {
                extensions[k] = f;
            }
        };
        registerIfAbsent("FixedDomainPredicateTypeResolver", function(type: string): DomainPredicateTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                return domainModel.types[type];
            }};
        });
        registerIfAbsent("FixedDomainOperationTypeResolver", function(type: string): DomainOperationTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                return domainModel.types[type];
            }};
        });
        registerIfAbsent("WidestDomainOperationTypeResolver", function(types: string[]): DomainOperationTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                for (let type of types) {
                    for (let domainType of domainTypes) {
                        if (domainType.name == type) {
                            return domainType;
                        }
                    }
                }

                return domainTypes.length == 0 ? domainModel.types[types[0]] : domainTypes[0];
            }};
        });
        registerIfAbsent("FirstArgumentDomainFunctionTypeResolver", function(type: string): DomainFunctionTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType {
                return argumentTypes.length == 0 ? null : domainModel.types[type];
            }};
        });
        registerIfAbsent("FixedDomainFunctionTypeResolver", function(type: string): DomainFunctionTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType {
                return domainModel.types[type];
            }};
        });
        registerIfAbsent("WidestDomainFunctionTypeResolver", function(types: string[]): DomainFunctionTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType {
                for (let type of types) {
                    for (let domainType of argumentTypes) {
                        if (domainType.name == type) {
                            return domainType;
                        }
                    }
                }

                return argumentTypes.length == 0 ? domainModel.types[types[0]] : argumentTypes[0];
            }};
        });
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
        let parseOp = function(op: string): DomainOperator {
            switch (op) {
                case 'M':
                    return DomainOperator.UNARY_MINUS;
                case 'P':
                    return DomainOperator.UNARY_PLUS;
                case '/':
                    return DomainOperator.DIVISION;
                case '-':
                    return DomainOperator.MINUS;
                case '%':
                    return DomainOperator.MODULO;
                case '*':
                    return DomainOperator.MULTIPLICATION;
                case '!':
                    return DomainOperator.NOT;
                case '+':
                    return DomainOperator.PLUS;
            }
            return null;
        };
        let parsePred = function(pred: string): DomainPredicate {
            switch (pred) {
                case 'C':
                    return DomainPredicate.COLLECTION;
                case 'E':
                    return DomainPredicate.EQUALITY;
                case 'N':
                    return DomainPredicate.NULLNESS;
                case 'R':
                    return DomainPredicate.RELATIONAL;
            }
            return null;
        };
        let resolver = function(resolver) {
            if (resolver !== undefined) {
                let typeResolver = null, args = [];
                if (typeof resolver === "string") {
                    typeResolver = extensions[resolver];
                } else {
                    for (let prop in resolver) {
                        if ((typeResolver = extensions[prop]) != null) {
                            args = resolver[prop];
                            break;
                        }
                    }
                }
                if (typeResolver != null) {
                    return typeResolver(args);
                }
            }
            return null;
        };
        let json = JSON.parse(input), types = json['types'], functions = json['funcs'], opResolvers = json['opResolvers'], predResolvers = json['predResolvers'];
        var domainTypes: StringMap<DomainType> = {};
        types.forEach(function (type) {
            let name = type['name'];
            let ops: DomainOperator[] = [];
            if (Array.isArray(type['ops'])) {
                type['ops'].forEach(function (op) {
                    let o = parseOp(op);
                    if (o != null) {
                        ops.push(o);
                    }
                });
            }
            let preds: DomainPredicate[] = [];
            if (Array.isArray(type['preds'])) {
                type['preds'].forEach(function (pred) {
                    let p = parsePred(pred);
                    if (p != null) {
                        preds.push(p);
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
                let resultTypeResolver: DomainFunctionTypeResolver = resolver(func['typeResolver']);
                let resultType: DomainType = null;
                if (resultTypeResolver == null) {
                    resultType = domainTypes[func['type']];
                }
                funcs[func['name']] = new DomainFunction(func['name'], func['minArgCount'], func['argCount'], resultType, resultTypeResolver, doc(meta), params, meta);
            });
        }
        let operationTypeResolvers: StringMap<StringMap<DomainOperationTypeResolver>> = {};
        if (Array.isArray(opResolvers)) {
            opResolvers.forEach(function (op) {
                let typeOps = op['typeOps'];
                let r: DomainOperationTypeResolver = resolver(op['resolver']);
                if (r != null) {
                    for (let prop in typeOps) {
                        if (domainTypes[prop] != null && Array.isArray(typeOps[prop])) {
                            let opMap = operationTypeResolvers[prop];
                            if (opMap == null) {
                                opMap = operationTypeResolvers[prop] = {};
                            }

                            typeOps[prop].forEach(function(op) {
                                let o = parseOp(op);
                                if (o != null) {
                                    opMap[DomainOperator[o]] = r;
                                }
                            });

                        }
                    }
                }
            });
        }
        let predicateTypeResolvers: StringMap<StringMap<DomainPredicateTypeResolver>> = {};
        if (Array.isArray(predResolvers)) {
            predResolvers.forEach(function (pred) {
                let typePreds = pred['typePreds'];
                let r: DomainPredicateTypeResolver = resolver(pred['resolver']);
                if (r != null) {
                    if (typePreds === undefined) {
                        // Special case that will just use the resolver for all registered type predicates
                        for (let name in domainTypes) {
                            let predMap = predicateTypeResolvers[name];
                            if (predMap == null) {
                                predMap = predicateTypeResolvers[name] = {};
                            }
                            for (let p of domainTypes[name].enabledPredicates) {
                                predMap[DomainPredicate[p]] = r;
                            }
                        }
                    } else {
                        for (let prop in typePreds) {
                            if (domainTypes[prop] != null && Array.isArray(typePreds[prop])) {
                                let predMap = predicateTypeResolvers[prop];
                                if (predMap == null) {
                                    predMap = predicateTypeResolvers[prop] = {};
                                }

                                typePreds[prop].forEach(function (pred) {
                                    let p = parsePred(pred);
                                    if (p != null) {
                                        predMap[DomainPredicate[p]] = r;
                                    }
                                });
                            }
                        }
                    }
                }
            });
        }

        return {
            types:                      domainTypes,
            functions:                  funcs,
            operationTypeResolvers:     operationTypeResolvers,
            predicateTypeResolvers:     predicateTypeResolvers,
            booleanLiteralResolver:     resolver(json['booleanLiteralResolver']),
            numericLiteralResolver:     resolver(json['numericLiteralResolver']),
            stringLiteralResolver:      resolver(json['stringLiteralResolver']),
            temporalLiteralResolver:    resolver(json['temporalLiteralResolver']),
            entityLiteralResolver:      resolver(json['entityLiteralResolver']),
            enumLiteralResolver:        resolver(json['enumLiteralResolver']),
            collectionLiteralResolver:  resolver(json['collectionLiteralResolver'])
        };
    }
}