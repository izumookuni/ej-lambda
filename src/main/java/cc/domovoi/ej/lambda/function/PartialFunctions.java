package cc.domovoi.ej.lambda.function;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class contains various methods for manipulating PartialFunction.
 */
public class PartialFunctions {

    /**
     * Create a PartialFunction instance without judgment logic.
     *
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @return A PartialFunction instance without judgment logic.
     */
    public static <A, B> PartialFunction<A, B> empty() {
        return PartialFunctionCompanion.empty();
    }

    /**
     * Create a PartialFunction instance.
     *
     * @param isDefinedAt Checks if a value is contained in the function's domain.
     * @param apply       Applies this function to the given argument.
     * @param <A>         the type of the input to the function.
     * @param <B>         the type of the result of the function.
     * @return A PartialFunction instance.
     */
    public static <A, B> PartialFunction<A, B> of(Predicate<A> isDefinedAt, Function<A, B> apply) {
        return new PartialFunction<A, B>() {
            @Override
            public Boolean isDefinedAt(A a) {
                return isDefinedAt.test(a);
            }

            @Override
            public B apply(A a) {
                return apply.apply(a);
            }
        };
    }

    /**
     * Create a PartialFunction from an existing function instance.
     *
     * @param f   An existing function instance.
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @return A PartialFunction instance.
     */
    public static <A, B> PartialFunction<A, B> from(Function<A, Optional<B>> f) {
        return unlift(f);
    }

    /**
     * Create a PartialFunction from a serials of existing function instances.
     *
     * @param fs  A serials of existing function instances.
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @return A PartialFunction instance.
     */
    public static <A, B> PartialFunction<A, B> fromAll(Collection<Function<A, Optional<B>>> fs) {
        return fs.stream().map(PartialFunctions::from).reduce(empty(), PartialFunction::orElse);
    }

    /**
     * Create a PartialFunction instance with end mark.
     *
     * @param isDefinedAt Checks if a value is contained in the function's domain.
     * @param apply       Applies this function to the given argument.
     * @param <A>         the type of the input to the function.
     * @param <B>         the type of the result of the function.
     * @return A PartialFunction instance with end mark.
     */
    public static <A, B> PartialFunction<A, B> withEnd(Predicate<A> isDefinedAt, Function<A, B> apply) {
        return of(isDefinedAt, apply).end();
    }

    /**
     * Create a PartialFunction from an existing function instance and with end mark.
     *
     * @param f   An existing function instance.
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @return A PartialFunction instance with end mark.
     */
    public static <A, B> PartialFunction<A, B> fromWithEnd(Function<A, Optional<B>> f) {
        return from(f).end();
    }

    /**
     * Create a PartialFunction from a serials of existing function instances and with end mark.
     *
     * @param fs  A serials of existing function instances.
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @return A PartialFunction instance with end mark.
     */
    public static <A, B> PartialFunction<A, B> fromAllWithEnd(Collection<Function<A, Optional<B>>> fs) {
        return fromAll(fs).end();
    }

    /**
     * Lift a partial function to normal function.
     *
     * @param pf  A PartialFunction instance.
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @return A Function instance.
     */
    public static <A, B> Function<A, Optional<B>> lift(PartialFunction<A, B> pf) {
        return pf.lift();
    }

    /**
     * Unlift a normal function to partial function.
     *
     * @param f   A Function instance.
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @return A PartialFunction instance.
     */
    public static <A, B> PartialFunction<A, B> unlift(Function<A, Optional<B>> f) {
        return PartialFunctionCompanion.unlifted(f);
    }

    /**
     * Creates a Boolean test based on a value and a partial function.
     * It behaves like a 'match' statement with an implied 'case _ =&gt; false'
     * following the supplied cases.
     *
     * @param x   the value to test
     * @param pf  the partial function
     * @param <T> the type of the input to the function.
     * @return true, iff `x` is in the domain of `pf` and `pf(x) == true`.
     */
    public static <T> Boolean cond(T x, PartialFunction<T, Boolean> pf) {
        return PartialFunctionCompanion.cond(x, pf);
    }

    /**
     * Transforms a PartialFunction[T, U] `pf` into Function1[T, Option[U]] `f`
     * whose result is `Some(x)` if the argument is in `pf`'s domain and `None`
     * otherwise, and applies it to the value `x`.  In effect, it is a
     * `'''match'''` statement which wraps all case results in `Some(_)` and
     * adds `'''case''' _ =&gt; None` to the end.
     *
     * @param x   the value to test
     * @param pf  the partial function
     * @param <T> the type of the input to the function.
     * @param <U> the type of the result of the function.
     * @return `Some(pf(x))` if `pf isDefinedAt x`, `None` otherwise.
     */
    public static <T, U> Optional<U> condOpt(T x, PartialFunction<T, U> pf) {
        return PartialFunctionCompanion.condOpt(x, pf);
    }
}
