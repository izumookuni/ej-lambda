package cc.domovoi.ej.lambda.function;

import java.util.function.Function;

/**
 * `AbstractPartialFunction` reformulates all operations of its supertrait `PartialFunction`
 * in terms of `isDefinedAt` and `applyOrElse`.
 * <p>
 * This allows more efficient implementations in many cases:
 * - optimized `orElse` method supports chained `orElse` in linear time,
 * and with no slow-down if the `orElse` part is not needed.
 * - optimized `lift` method helps to avoid double evaluation of pattern matchers and guards
 * of partial function literals.
 * <p>
 * This trait is used as a basis for implementation of all partial function literals.
 *
 * @param <T> the type of the input to the function.
 * @param <R> the type of the result of the function.
 */
public abstract class AbstractPartialFunction<T, R> implements Function<T, R>, PartialFunction<T, R> {
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     */
    @Override
    public R apply(T t) {
        return applyOrElse(t, PartialFunctionCompanion.empty());
    }
}
