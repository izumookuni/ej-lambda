package cc.domovoi.ej.lambda.function;

import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * @param <A> the type of the input to the function
 * @param <B> the type of the result of the function
 */
public interface EJFunction<A, B> extends Function<A, B> {
}
