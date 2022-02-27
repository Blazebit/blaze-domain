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
    COLLECTION,
    /**
     * A union domain type.
     */
    UNION
}

/**
 * The domain operators that are available for domain types.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export enum DomainOperator {
    /**
     * The <code>!</code> operator.
     */
    NOT,
    /**
     * The unary <code>+</code> operator.
     */
    UNARY_PLUS,
    /**
     * The unary <code>-</code> operator.
     */
    UNARY_MINUS,
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
     * The <code>+</code> operator.
     */
    PLUS,
    /**
     * The <code>-</code> operator.
     */
    MINUS
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
 * The volatility of a domain function.
 *
 * @author Christian Beikov
 * @since 2.0.3
 */
export enum DomainFunctionVolatility {
    /**
     * The function result can change at any time and is not just dependent on the arguments.
     */
    VOLATILE,
    /**
     * The function result depends on the arguments and possibly other state which stays stable throughout the execution.
     */
    STABLE,
    /**
     * The function result depends just on the arguments.
     */
    IMMUTABLE
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

    toString(): string {
        return this.name == null ? "n/a" : this.name;
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

    toString(): string {
        return this.name;
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

    constructor(name: string, elementType: DomainType) {
        super(name, DomainTypeKind.COLLECTION, [], [DomainPredicate.COLLECTION], []);
        this.elementType = elementType;
    }

    toString(): string {
        if (this.elementType == null) {
            return "Collection";
        } else {
            return "Collection[" + this.elementType + "]";
        }
    }
}

/**
 * A collection type in the domain.
 *
 * @author Christian Beikov
 * @since 2.0.6
 */
export class UnionDomainType extends DomainType {
    /**
     * The domain type of the collection element.
     */
    unionElements: DomainType[];

    constructor(name: string, enabledOperators: readonly DomainOperator[], enabledPredicates: readonly DomainPredicate[], metadata: any[]) {
        super(name, DomainTypeKind.UNION, enabledOperators, enabledPredicates, metadata);
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
     * The volatility of the domain function.
     */
    volatility: DomainFunctionVolatility;
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

    constructor(name: string, volatility: DomainFunctionVolatility, minArgumentCount: number, argumentCount: number, resultType: DomainType, resultTypeResolver: DomainFunctionTypeResolver, documentation: string, args: readonly DomainFunctionArgument[], metadata: any[]) {
        super(metadata);
        this.name = name;
        this.volatility = volatility;
        this.minArgumentCount = minArgumentCount;
        this.argumentCount = argumentCount;
        this.resultType = resultType;
        this.resultTypeResolver = resultTypeResolver;
        this.documentation = documentation;
        this.arguments = args;
    }

    toString(): string {
        let signature = this.name;
        signature += " (";
        if (this.arguments.length == 0) {
            if (this.minArgumentCount != 0) {
                for (var i = 0; i < this.minArgumentCount; i++) {
                    signature += "argument" + (i + 1) + ", ";
                }
                if (this.argumentCount < this.minArgumentCount) {
                    signature += "...";
                } else {
                    signature = signature.substring(0, signature.length - 2);
                }
            } else if (this.argumentCount > 0) {
                for (var i = 0; i < this.minArgumentCount; i++) {
                    signature += "argument" + (i + 1) + ", ";
                }
                signature = signature.substring(0, signature.length - 2);
            }
        } else {
            for (var i = 0; i < this.arguments.length; i++) {
                let argument = this.arguments[i];
                if (argument.name == null) {
                    signature += "argument" + (i + 1) + ", ";
                } else {
                    signature += argument.name + ", ";
                }
            }
            signature = signature.substring(0, signature.length - 2);
        }
        signature += ")";
        return signature;
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

    toString(): string {
        if (this.name == null) {
            return "DomainFunctionArgument{" +
                "index=" + this.position +
                ", type=" + this.type +
                '}';
        } else {
            return "DomainFunctionArgument{" +
                "name='" + this.name + '\'' +
                ", index=" + this.position +
                ", type=" + this.type +
                '}';
        }
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

    toString(): string {
        return this.value;
    }
}

/**
 * A domain type resolver exception.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class DomainTypeResolverException extends Error {
    constructor(message: string) {
        super(message);
        Object.setPrototypeOf(this, new.target.prototype);
    }

}

/**
 * A function type resolver exception.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class FunctionTypeResolverException extends DomainTypeResolverException {
    domainFunction: DomainFunction;
    argumentIndex: number;
    actualArgumentType: DomainType;
    supportedArgumentTypes: string[];

    constructor(message: string, domainFunction: DomainFunction, argumentIndex: number, actualArgType: DomainType, supportedArgumentTypes: string[]) {
        super(message);
        this.domainFunction = domainFunction;
        this.argumentIndex = argumentIndex;
        this.actualArgumentType = actualArgType;
        this.supportedArgumentTypes = supportedArgumentTypes;
    }

}

/**
 * An operand type resolver exception.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
export class OperandTypeResolverException extends DomainTypeResolverException {
    operandIndex: number;
    actualOperandType: DomainType;
    supportedOperandTypes: string[];

    constructor(message: string, operandIndex: number, actualOperandType: DomainType, supportedOperandTypes: string[]) {
        super(message);
        this.operandIndex = operandIndex;
        this.actualOperandType = actualOperandType;
        this.supportedOperandTypes = supportedOperandTypes;
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
     * The types of the domain model as map indexed by their type name.
     */
    private types: StringMap<DomainType>;
    /**
     * The collection types of the domain model as map indexed by their type name.
     */
    private collectionTypes: StringMap<CollectionDomainType>;
    /**
     * The functions of the domain model as map indexed by their function name.
     */
    private functions: StringMap<DomainFunction>;
    /**
     * Returns the operation type resolvers of the domain model as map indexed by their type name.
     */
    private operationTypeResolvers: StringMap<StringMap<DomainOperationTypeResolver>>;
    /**
     * Returns the predicate type resolvers of the domain model as map indexed by their type name.
     */
    private predicateTypeResolvers: StringMap<StringMap<DomainPredicateTypeResolver>>;

    private static readonly COLLECTION: CollectionDomainType = new CollectionDomainType("Collection", null);

    constructor(types: StringMap<DomainType>, collectionTypes: StringMap<CollectionDomainType>, functions: StringMap<DomainFunction>, operationTypeResolvers: StringMap<StringMap<DomainOperationTypeResolver>>, predicateTypeResolvers: StringMap<StringMap<DomainPredicateTypeResolver>>) {
        this.types = types;
        this.collectionTypes = collectionTypes;
        this.functions = functions;
        this.operationTypeResolvers = operationTypeResolvers;
        this.predicateTypeResolvers = predicateTypeResolvers;
    }

    getType(typeName: string): DomainType {
        return DomainModel.getType(typeName, this.types, this.collectionTypes);
    }

    getFunction(typeName: string): DomainFunction {
        return this.functions[typeName];
    }

    getOperationTypeResolvers(typeName: string): StringMap<DomainOperationTypeResolver> {
        return this.operationTypeResolvers[typeName];
    }

    getOperationTypeResolver(typeName: string, operator: DomainOperator): DomainOperationTypeResolver {
        return this.operationTypeResolvers[typeName][DomainOperator[operator]];
    }

    getPredicateTypeResolvers(typeName: string): StringMap<DomainPredicateTypeResolver> {
        return this.predicateTypeResolvers[typeName];
    }

    getPredicateTypeResolver(typeName: string, predicate: DomainPredicate): DomainPredicateTypeResolver {
        return this.predicateTypeResolvers[typeName][DomainPredicate[predicate]];
    }

    private static getType(typeName: string, types: StringMap<DomainType>, collectionTypes: StringMap<CollectionDomainType>): DomainType {
        if (typeName === undefined) {
            return null;
        }
        if (typeName.startsWith("Collection")) {
            if (typeName.length == "Collection".length) {
                return DomainModel.COLLECTION;
            } else if (typeName.charAt("Collection".length) == '[') {
                let elementTypeName = typeName.substring("Collection".length + 1, typeName.length - 1);
                if (collectionTypes[elementTypeName] !== undefined) {
                    return collectionTypes[elementTypeName];
                } else {
                    return collectionTypes[elementTypeName] = new CollectionDomainType(typeName, DomainModel.getType(elementTypeName, types, collectionTypes));
                }
            }
        }
        return types[typeName];
    }

    /**
     * Parses the given JSON string to a domain model.
     *
     * @param input The JSON string or object
     * @param baseModel The optional base model
     * @param extensions The optional extension functions like resolver constructors
     */
    static parse(input: object | string, baseModel?: DomainModel, extensions?: StringMap<Function>): DomainModel {
        let json;
        if (typeof input === "string") {
            json = JSON.parse(input);
        } else {
            json = input;
        }
        if (typeof extensions === "undefined") {
            extensions = {};
        }
        let registerIfAbsent = function(k: string, f: Function) {
            if (!(extensions[k] instanceof Function)) {
                extensions[k] = f;
            }
        };
        let validateArgumentTypes = function(domainFunction: DomainFunction, argumentTypes: DomainType[]) {
            OUTER: for (var i = 0; i < argumentTypes.length; i++) {
                let functionArgument = domainFunction.arguments[i];
                let argType = argumentTypes[i];
                if (functionArgument.type == null || argType == null) {
                    continue;
                }
                if (functionArgument.type instanceof CollectionDomainType && argType instanceof CollectionDomainType) {
                    if (functionArgument.type.elementType == null || argType.elementType == null) {
                        continue;
                    }
                }
                if (functionArgument.type instanceof UnionDomainType) {
                    let unionElements = functionArgument.type.unionElements;
                    if (argType instanceof CollectionDomainType) {
                        for (const unionElement of unionElements) {
                            if (unionElement == argType || unionElement instanceof CollectionDomainType && (unionElement as CollectionDomainType).elementType == null) {
                                continue OUTER;
                            }
                        }
                    } else {
                        for (const unionElement of unionElements) {
                            if (unionElement == argType) {
                                continue OUTER;
                            }
                        }
                    }
                }
                if (functionArgument.type != argType) {
                    throw new FunctionTypeResolverException("Unsupported argument type '" + argType + "' for argument '" + functionArgument + "' of function '" + domainFunction.name + "'! Expected type: " + functionArgument.type, domainFunction, i, argType, [functionArgument.type.name]);
                }
            }
        };
        registerIfAbsent("FixedDomainPredicateTypeResolver", function(type: string): DomainPredicateTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                return domainModel.getType(type);
            }};
        });
        registerIfAbsent("RestrictedDomainPredicateTypeResolver", function(returningType: string, supportedTypes: string[]): DomainPredicateTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                for (var i = 0; i < domainTypes.length; i++) {
                    let domainType = domainTypes[i];
                    if (supportedTypes.indexOf(domainType.name) == -1) {
                        let typesString = "[";
                        for (let supportedType of supportedTypes) {
                            typesString += supportedType + ", ";
                        }
                        typesString = typesString.substring(0, typesString.length - 2) + "]";
                        throw new OperandTypeResolverException("The predicate operand at index " + i + " with the domain type '" + domainType.name + "' is unsupported! Expected one of the following types: " + typesString, i, domainType, supportedTypes);
                    }
                }
                return domainModel.getType(returningType);
            }};
        });
        registerIfAbsent("OperandRestrictedDomainPredicateTypeResolver", function(returningType: string, supportedTypesPerOperand: string[][]): DomainPredicateTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                for (var i = 0; i < domainTypes.length; i++) {
                    let supportedTypes = supportedTypesPerOperand[i];
                    let domainType = domainTypes[i];
                    if (supportedTypes.indexOf(domainType.name) == -1) {
                        let typesString = "[";
                        for (let supportedType of supportedTypes) {
                            typesString += supportedType + ", ";
                        }
                        typesString = typesString.substring(0, typesString.length - 2) + "]";
                        throw new OperandTypeResolverException("The predicate operand at index " + i + " with the domain type '" + domainType.name + "' is unsupported! Expected one of the following types: " + typesString, i, domainType, supportedTypes);
                    }
                }
                return domainModel.getType(returningType);
            }};
        });
        registerIfAbsent("FixedDomainOperationTypeResolver", function(type: string): DomainOperationTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                return domainModel.getType(type);
            }};
        });
        registerIfAbsent("WidestDomainOperationTypeResolver", function(supportedTypes: string[]): DomainOperationTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                let typeIndex = Number.MAX_VALUE;
                for (var i = 0; i < domainTypes.length; i++) {
                    let domainType = domainTypes[i];
                    let idx = supportedTypes.indexOf(domainType.name);
                    if (idx == -1) {
                        let typesString = "[";
                        for (let supportedType of supportedTypes) {
                            typesString += supportedType + ", ";
                        }
                        typesString = typesString.substring(0, typesString.length - 2) + "]";
                        throw new OperandTypeResolverException("The operation operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following types: " + typesString, i, domainType, supportedTypes);
                    }
                    typeIndex = Math.min(typeIndex, idx);
                }

                if (typeIndex == Number.MAX_VALUE) {
                    return domainModel.getType(supportedTypes[0]);
                } else {
                    return domainModel.getType(supportedTypes[typeIndex]);
                }
            }};
        });
        registerIfAbsent("RestrictedDomainOperationTypeResolver", function(returningType: string, supportedTypes: string[]): DomainOperationTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                for (var i = 0; i < domainTypes.length; i++) {
                    let domainType = domainTypes[i];
                    if (supportedTypes.indexOf(domainType.name) == -1) {
                        let typesString = "[";
                        for (let supportedType of supportedTypes) {
                            typesString += supportedType + ", ";
                        }
                        typesString = typesString.substring(0, typesString.length - 2) + "]";
                        throw new OperandTypeResolverException("The operation operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following types: " + typesString, i, domainType, supportedTypes);
                    }
                }

                return domainModel.getType(returningType);
            }};
        });
        registerIfAbsent("OperandRestrictedDomainOperationTypeResolver", function(returningType: string, supportedTypesPerOperand: string[][]): DomainOperationTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainTypes: DomainType[]): DomainType {
                for (var i = 0; i < domainTypes.length; i++) {
                    let supportedTypes = supportedTypesPerOperand[i];
                    let domainType = domainTypes[i];
                    if (supportedTypes.indexOf(domainType.name) == -1) {
                        let typesString = "[";
                        for (let supportedType of supportedTypes) {
                            typesString += supportedType + ", ";
                        }
                        typesString = typesString.substring(0, typesString.length - 2) + "]";
                        throw new OperandTypeResolverException("The operation operand at index " + i + " with the domain type '" + domainType + "' is unsupported! Expected one of the following types: " + typesString, i, domainType, supportedTypes);
                    }
                }

                return domainModel.getType(returningType);
            }};
        });
        registerIfAbsent("NthArgumentDomainFunctionTypeResolver", function(index: number): DomainFunctionTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType {
                validateArgumentTypes(domainFunction, argumentTypes);
                return argumentTypes.length > index ? argumentTypes[index] : null;
            }};
        });
        registerIfAbsent("FixedDomainFunctionTypeResolver", function(type: string): DomainFunctionTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType {
                validateArgumentTypes(domainFunction, argumentTypes);
                return domainModel.getType(type);
            }};
        });
        registerIfAbsent("WidestDomainFunctionTypeResolver", function(types: string[]): DomainFunctionTypeResolver {
            return { resolveType: function(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType {
                let typeIndex = Number.MAX_VALUE;
                for (var i = 0; i < argumentTypes.length; i++) {
                    let domainType = argumentTypes[i];
                    let idx = types.indexOf(domainType.name);
                    if (idx == -1) {
                        let typesString = "[";
                        for (let supportedType of types) {
                            typesString += supportedType + ", ";
                        }
                        typesString = typesString.substring(0, typesString.length - 2) + "]";
                        throw new FunctionTypeResolverException("Unsupported argument type '" + domainType + "' for argument '" + domainFunction.arguments[i] + "' of function '" + domainFunction.name + "'! Expected one of the following types: " + typesString, domainFunction, i, domainType, types);
                    }
                    typeIndex = Math.min(typeIndex, idx);
                }

                if (typeIndex == Number.MAX_VALUE) {
                    return domainModel.getType(types[0]);
                } else {
                    return domainModel.getType(types[typeIndex]);
                }
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
                    return typeResolver(...args);
                }
            }
            return null;
        };
        let types = json['types'], functions = json['funcs'], opResolvers = json['opResolvers'], predResolvers = json['predResolvers'];
        var domainTypes: StringMap<DomainType> = {};
        var collectionTypes: StringMap<CollectionDomainType> = {};
        var funcs: StringMap<DomainFunction> = {};
        let operationTypeResolvers: StringMap<StringMap<DomainOperationTypeResolver>> = {};
        let predicateTypeResolvers: StringMap<StringMap<DomainPredicateTypeResolver>> = {};
        if (baseModel instanceof DomainModel) {
            let t = baseModel.types;
            for (let name in t) {
                domainTypes[name] = t[name];
            }
            let f = baseModel.functions;
            for (let name in f) {
                funcs[name] = f[name];
            }
            let o = baseModel.operationTypeResolvers;
            for (let name in o) {
                let r = operationTypeResolvers[name] = {};
                let m = o[name];
                for (let op in m) {
                    r[op] = m[op];
                }
            }
            let p = baseModel.predicateTypeResolvers;
            for (let name in p) {
                let r = predicateTypeResolvers[name] = {};
                let m = p[name];
                for (let op in m) {
                    r[op] = m[op];
                }
            }
        }
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
            let oldDomainType = domainTypes[name];
            let newDomainType;
            switch (type['kind']) {
                case 'B':
                    newDomainType = new BasicDomainType(name, ops, preds, meta);
                    break;
                case 'C':
                    // Ignore, since we build these types lazily
                    return;
                case 'U':
                    newDomainType = new UnionDomainType(name, ops, preds, meta);
                    break;
                case 'E':
                    var attrs: StringMap<EntityAttribute> = {};
                    newDomainType = new EntityDomainType(name, ops, preds, attrs, meta);
                    break;
                case 'N':
                    var vals: StringMap<EnumDomainTypeValue> = {};
                    type['vals'].forEach(function (val) {
                        let valMeta = parseMeta(val['meta']);
                        vals[val['name']] = new EnumDomainTypeValue(val['name'], doc(valMeta), valMeta);
                    });
                    newDomainType = new EnumDomainType(name, ops, preds, vals, meta);
                    break;
                case 'T':
                default:
                    newDomainType = null;
                    break;
            }
            if (oldDomainType !== undefined) {
                if (newDomainType == null) {
                    operationTypeResolvers[name] = null;
                    predicateTypeResolvers[name] = null;
                } else {
                    let operationResolvers = operationTypeResolvers[name];
                    if (operationResolvers !== undefined) {
                        for (const enabledOperator of oldDomainType.enabledOperators) {
                            if (newDomainType.enabledOperators.indexOf(enabledOperator) == -1) {
                                operationResolvers[DomainOperator[enabledOperator]] = null;
                            }
                        }
                    }
                    let predicateResolvers = predicateTypeResolvers[name];
                    if (predicateResolvers !== undefined) {
                        for (const enabledPredicate of oldDomainType.enabledPredicates) {
                            if (newDomainType.enabledPredicates.indexOf(enabledPredicate) == -1) {
                                predicateResolvers[DomainPredicate[enabledPredicate]] = null;
                            }
                        }
                    }
                }
            }
            domainTypes[name] = newDomainType;
        });
        types.forEach(function (type) {
            let name = type['name'];
            switch (type['kind']) {
                case 'E':
                    let entityType = domainTypes[name] as EntityDomainType;
                    if (Array.isArray(type['attrs'])) {
                        type['attrs'].forEach(function (a) {
                            let attrMeta = parseMeta(a['meta']);
                            entityType.attributes[a['name']] = new EntityAttribute(a['name'], DomainModel.getType(a['type'], domainTypes, collectionTypes), doc(attrMeta), attrMeta);
                        });
                    }
                    break;
                case 'C':
                    // Ignore, since we build these types lazily
                    return;
                case 'U':
                    let unionType = domainTypes[name] as UnionDomainType;
                    let unionElementTypeNames = unionType.name.split('|');
                    unionType.unionElements = [];
                    for (const unionElementTypeName of unionElementTypeNames) {
                        unionType.unionElements.push(DomainModel.getType(unionElementTypeName, domainTypes, collectionTypes))
                    }
                    break;
            }
        });
        if (Array.isArray(functions)) {
            functions.forEach(function (func) {
                if (func['volatility'] === undefined) {
                    funcs[func['name']] = null;
                    return;
                }
                var params: DomainFunctionArgument[] = [];
                let args = func['args'];
                if (Array.isArray(args)) {
                    for (var i = 0; i < args.length; i++) {
                        let param = args[i];
                        let paramMeta = parseMeta(param['meta']);
                        params.push(new DomainFunctionArgument(param['name'], i, DomainModel.getType(param['type'], domainTypes, collectionTypes), doc(paramMeta), paramMeta));
                    }
                }
                let meta = parseMeta(func['meta']);
                let resultTypeResolver: DomainFunctionTypeResolver = resolver(func['typeResolver']);
                let resultType: DomainType = null;
                if (resultTypeResolver == null) {
                    resultType = DomainModel.getType(func['type'], domainTypes, collectionTypes);
                    resultTypeResolver = {
                        resolveType(domainModel: DomainModel, domainFunction: DomainFunction, argumentTypes: DomainType[]): DomainType {
                            validateArgumentTypes(domainFunction, argumentTypes);
                            return domainFunction.resultType;
                        }
                    };
                }
                let volatility: DomainFunctionVolatility = DomainFunctionVolatility.IMMUTABLE;
                switch (func['volatility']) {
                    case 'I':
                        volatility = DomainFunctionVolatility.IMMUTABLE;
                        break;
                    case 'S':
                        volatility = DomainFunctionVolatility.STABLE;
                        break;
                    case 'V':
                        volatility = DomainFunctionVolatility.VOLATILE;
                        break;
                }
                funcs[func['name']] = new DomainFunction(func['name'], volatility, func['minArgCount'], func['argCount'], resultType, resultTypeResolver, doc(meta), params, meta);
            });
        }
        if (Array.isArray(opResolvers)) {
            opResolvers.forEach(function (op) {
                let typeOps = op['typeOps'];
                let r: DomainOperationTypeResolver = resolver(op['resolver']);
                if (r != null) {
                    for (let prop in typeOps) {
                        if (domainTypes[prop] != null) {
                            if (Array.isArray(typeOps[prop])) {
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
                }
            });
        }
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
                            if (domainTypes[prop] != null) {
                                if (Array.isArray(typePreds[prop])) {
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
                }
            });
        }

        return new DomainModel(
            domainTypes,
            collectionTypes,
            funcs,
            operationTypeResolvers,
            predicateTypeResolvers
        );
    }
}