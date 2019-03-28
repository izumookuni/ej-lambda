package cc.domovoi.lambda.function;

import java.util.function.Function;

/**
 * Represents a function that accepts one argument and produces a result.
 *
 * @param <A> the type of the input to the function
 * @param <B> the type of the result of the function
 */
public interface EJFunction<A, B> extends Function<A, B> {

    static <AA, BB> EJFunction<AA, BB> from(Function<? super AA, ? extends BB> function) {
        return function::apply;
    }

    default B applyNull(A a) {
        return null;
    }

    default <C> Function<A, Function<B, C>> andThenCurrying(Function<? super B, ? extends C> after) {
        return (A a) -> (B b) -> after.apply(apply(a));
    }

    default <C> Function<C, Function<A, B>> composeCurrying(Function<? super C, ? extends A> before) {
        return (C c) -> (A a) -> apply(before.apply(c));
    }
}
