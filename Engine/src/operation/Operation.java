package operation;

import exception.InvalidFunctionArgument;
import expression.api.Expression;
import expression.impl.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

public enum Operation {


    PLUS(BinaryExpression.numberOfArgs,"{PLUS,<number/numeric function>,<number/numeric function>}")
        {
            @Override
           public Expression create(Object... args)
            {
                return (Expression) createInstance(Plus.class, getNumberOfArguments(),args);
            }
        },

    MINUS(BinaryExpression.numberOfArgs,"{MINUS,<number/numeric function>,<number/numeric function>}")
        {
            @Override
            public Expression create(Object... args)
            {
                return (Expression) createInstance(Minus.class, getNumberOfArguments(),args);
            }
        },

    TIMES(BinaryExpression.numberOfArgs,"{TIMES,<number/numeric function>,<number/numeric function>}"){
        @Override
        public Expression create(Object... args) {
            return  (Expression) createInstance(Minus.class, getNumberOfArguments(),args);
        }
    },

    DIVIDE(BinaryExpression.numberOfArgs,"{DIVIDE,<number/numeric function>,<number/numeric function>}"){
        @Override
        public Expression create(Object... args) {
            return (Expression) createInstance(Divide.class, getNumberOfArguments(),args);
        }
    },
    CONCAT(BinaryExpression.numberOfArgs,"{CONCAT,<string/string function>,<string/string function>}"){
        @Override
        public Expression create(Object... args) {
            return (Expression) createInstance(Concat.class, getNumberOfArguments(),args);
        }
    },
    POW(BinaryExpression.numberOfArgs,"{MOD,<number/numeric function>,<number/numeric function>}"){
        @Override
        public Expression create(Object... args) {
            return (Expression) createInstance(Pow.class, getNumberOfArguments(),args);
        }
    },
    MOD(BinaryExpression.numberOfArgs,"{MOD,<number/numeric function>,<number/numeric function>}"){
        @Override
        public Expression create(Object... args) {
            return (Expression) createInstance(Mod.class, getNumberOfArguments(),args);
        }
    },
    SUB(3,"{SUB,<string/string function>,<number/numeric function>,<number/numeric function>}"){
        @Override
        public Expression create(Object... args) {
            return (Expression) createInstance(Sub.class, getNumberOfArguments(),args);
        }
    },
    ABS(UnaryExpression.numberOfArgs,"{ABS,<number/numeric function>}"){
        @Override
        public Expression create(Object... args) {
            return (Expression) createInstance(Abs.class, getNumberOfArguments(),args);
        }
    },
    REF(UnaryExpression.numberOfArgs,"{REF,<cell-id>}"){
        @Override
        public Expression create(Object... args) {
            return (Expression) createInstance(Ref.class, getNumberOfArguments(),args);
        }

    };

    private final int numberOfArguments;
    private final String description;
    private final Operation[] allOperation = Operation.values();

    Operation (int args, String description) {
        this.numberOfArguments = args;
        this.description = description;
    }

private static Object createInstance(Class<?> clazz, int numberOfArgs, Object... args) {

        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == numberOfArgs)
                .findFirst()
                .map(constructor -> {
                    try {
                        constructor.setAccessible(true);
                        return constructor.newInstance(args);
                    } catch (InstantiationException e) {
                        InvalidFunctionArgument funcError =  new InvalidFunctionArgument(valueOf(clazz.getSimpleName().toUpperCase()), List.of(args));
                        funcError.initCause(e);
                        throw funcError;
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        InvalidFunctionArgument funcError =  new InvalidFunctionArgument(valueOf(clazz.getSimpleName().toUpperCase()), List.of(args));
                        funcError.initCause(e);
                        throw funcError;
                    }
                    catch (IllegalArgumentException e) {
                        InvalidFunctionArgument funcError =  new InvalidFunctionArgument(valueOf(clazz.getSimpleName().toUpperCase()), List.of(args));
                        funcError.initCause(e);
                        throw funcError;
                    }
                })
                .get();

    }

    public int getNumberOfArguments() {
        return numberOfArguments;
    }

    public abstract Expression create(Object... args);
}
