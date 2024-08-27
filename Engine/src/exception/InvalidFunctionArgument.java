package exception;

import operation.Operation;

import java.util.List;

public class InvalidFunctionArgument extends RuntimeException {

    Operation function;
    List<Object> arguments;

    public InvalidFunctionArgument(Operation function, List<Object> arguments) {
        super("Invalid value input:\nfunction name:" + function.name() + "\narguments:" + arguments.toString() +
                "\nexample for valid function:\n" + function.toString());
        this.arguments = arguments;
        this.function = function;
    }

    @Override
    public String toString() {
        return getMessage();
    }

}
