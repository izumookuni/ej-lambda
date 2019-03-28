package cc.domovoi.lambda.function;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A partial function of type `PartialFunction[A, B]` is a unary function
 * where the domain does not necessarily include all values of type `A`.
 * The function `isDefinedAt` allows to test dynamically if a value is in
 * the domain of the function.
 * <p>
 * It is the responsibility of the caller to call `isDefinedAt` before
 * calling `apply`, because if `isDefinedAt` is false, it is not guaranteed
 * `apply` will throw an exception to indicate an error condition. If an
 * exception is not thrown, evaluation may result in an arbitrary value.
 *
 * @param <A> the type of the input to the function.
 * @param <B> the type of the result of the function.
 */
public interface PartialFunction<A, B> extends Function<A, B>, EJFunction<A, B> {

    /**
     * Checks if a value is contained in the function's domain.
     *
     * @param a the value to test.
     * @return `true`, iff `a` is in the domain of this function, `false` otherwise.
     */
    Boolean isDefinedAt(A a);

    /**
     * Composes this partial function with a fallback partial function which
     * gets applied where this partial function is not defined.
     *
     * @param that the fallback function.
     * @return a partial function which has as domain the union of the domains
     * of this partial function and `that`. The resulting partial function
     * takes `a` to `this.apply(a)` where `this` is defined, and to `that.apply(a)` where it is not.
     */
    @SuppressWarnings("unchecked")
    default PartialFunction<A, B> orElse(PartialFunction<? super A, ? extends B> that) {
        return new PartialFunctionCompanion.OrElse<>(this, (PartialFunction<A, B>) that);
    }

    /**
     * Composes this partial function with a transformation function that
     * gets applied to results of this partial function.
     *
     * @param after the transformation function.
     * @param <V>   the result type of the transformation function.
     * @return a partial function with the same domain as this partial function, which maps
     * arguments `a` to `after.apply(this.apply(a))`.
     */
    @Override
    @SuppressWarnings("unchecked")
    default <V> PartialFunction<A, V> andThen(Function<? super B, ? extends V> after) {
        return new PartialFunctionCompanion.AndThen<>(this, (PartialFunction<B, V>) after);
    }

    /**
     * Returns a composed function that first applies the {@code before}
     * function to its input, and then applies this function to the result.
     * If evaluation of either function throws an exception, it is relayed to
     * the caller of the composed function.
     *
     * @param before the function to apply before this function is applied.
     * @param <V>    the type of input to the {@code before} function, and to the
     *               composed function.
     * @return a composed function that first applies the {@code before}
     * function and then applies this function.
     */
    @Override
    @SuppressWarnings("unchecked")
    default <V> PartialFunction<V, B> compose(Function<? super V, ? extends A> before) {
        return new PartialFunctionCompanion.AndThen<>((PartialFunction<V, A>) before, this);
    }

    /**
     * Turns this partial function into a plain function returning an `Optional` result.
     *
     * @return a function that takes an argument `a` to `Optional.of(this.apply(a))` if `this`
     * is defined for `a`, and to `Optional.empty` otherwise.
     */
    default Function<A, Optional<B>> lift() {
        return new PartialFunctionCompanion.Lifted<>(this);
    }

    /**
     * Applies this partial function to the given argument when it is contained in the function domain.
     * Applies fallback function where this partial function is not defined.
     *
     * @param a    the function argument.
     * @param zero the fallback function.
     * @return the result of this function or fallback function application.
     */
    default B applyOrElse(A a, Function<? super A, ? extends B> zero) {
        if (isDefinedAt(a)) {
            return apply(a);
        } else {
            return zero.apply(a);
        }
    }

    /**
     * The end mark of PartialFunction, indicates the end of PartialFunction judgment logic.
     * If fp does not end with ".end()", the final judgment logic will be applied.
     *
     * @return PartialFunction with end mark.
     */
    default PartialFunction<A, B> end() {
        return this.orElse(PartialFunctionCompanion.empty());
    }

    /**
     * Composes this partial function with an action function which
     * gets applied to results of this partial function.
     * The action function is invoked only for its side effects; its result is ignored.
     *
     * @param action the action function.
     * @param <U>    unit.
     * @return a function which maps arguments `a` to `isDefinedAt(a)`. The resulting function
     * runs `action.apply(this.apply(a))` where `this` is defined.
     */
    default <U> Function<A, Boolean> runWith(Function<? super B, ? extends U> action) {
        return (x) -> {
            B z = applyOrElse(x, PartialFunctionCompanion.checkFallback());
            if (!PartialFunctionCompanion.fallbackOccurred(z)) {
                action.apply(z);
                return true;
            } else {
                return false;
            }
        };
    }

    /**
     * Composes this partial function with a fallback partial function from a given
     * `isDefinedAt` and `apply` which gets applied where this partial function is not defined.
     *
     * @param isDefinedAt Checks if a value is contained in the function's domain.
     * @param apply       Applies this function to the given argument.
     * @return a partial function which has as domain the union of the domains
     * of this partial function and another function.
     */
    default PartialFunction<A, B> orElseOf(Predicate<A> isDefinedAt, Function<A, B> apply) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.of(isDefinedAt, apply));
    }

    /**
     * Composes this partial function with a fallback partial function from a Function instance which
     * gets applied where this partial function is not defined.
     *
     * @param f a Function instance with a Optional result, which will return `Optional.of`
     *          if a value is contained in the function's domain, otherwise return `Optional.empty`
     * @return a partial function which has as domain the union of the domains
     * of this partial function and `f`.
     */
    default PartialFunction<A, B> orElseFrom(Function<A, Optional<B>> f) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.from(f));
    }

    /**
     * Composes this partial function with a transformation function from a given `isDefinedAt` and `apply`
     * that gets applied to results of this partial function.
     *
     * @param isDefinedAt Checks if a value is contained in the function's domain.
     * @param apply       Applies this function to the given argument.
     * @param <V>         the result type of the transformation function.
     * @return a partial function with the same domain as this partial function, which maps
     * arguments `a` to `apply(this.apply(a))`.
     */
    default <V> PartialFunction<A, V> andThenOf(Predicate<B> isDefinedAt, Function<B, V> apply) {
        return new PartialFunctionCompanion.AndThen<>(this, PartialFunctions.of(isDefinedAt, apply));
    }

    /**
     * Composes this partial function with a transformation function from a Function instance that
     * gets applied to results of this partial function.
     *
     * @param f   the transformation function.
     * @param <V> the result type of the transformation function.
     * @return a partial function with the same domain as this partial function, which maps
     * arguments `a` to `f.apply(this.apply(a))`.
     */
    default <V> PartialFunction<A, V> andThenFrom(Function<B, Optional<V>> f) {
        return new PartialFunctionCompanion.AndThen<>(this, PartialFunctions.from(f));
    }

    /**
     * When all the judgment logic is not satisfied, return the default value.
     *
     * @param b default value.
     * @return PartialFunction with a default end value.
     */
    default PartialFunction<A, B> orEndWith(B b) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.of(a -> true, a -> b));
    }

    /**
     * When all the judgment logic is not satisfied, return the default lazy evaluation value.
     *
     * @param f default lazy evaluation value.
     * @return PartialFunction with a default lazy evaluation end value.
     */
    default PartialFunction<A, B> orDefault(Supplier<B> f) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.of(a -> true, a -> f.get()));
    }
}
