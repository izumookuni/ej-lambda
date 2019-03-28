package cc.domovoi.lambda.function;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

/**
 * A few handy operations which leverage the extra bit of information
 * available in partial functions.
 */
public class PartialFunctionCompanion {

    /**
     * To implement patterns like {@code if(pf isDefinedAt x) f1(pf(x)) else f2(x) } efficiently
     * the following trick is used:
     * <p>
     * To avoid double evaluation of pattern matchers & guards `applyOrElse` method is used here
     * instead of `isDefinedAt`/`apply` pair.
     * <p>
     * After call to `applyOrElse` we need both the function result it returned and
     * the fact if the function's argument was contained in its domain. The only degree of freedom we have here
     * to achieve this goal is tweaking with the continuation argument (`default`) of `applyOrElse` method.
     * The obvious way is to throw an exception from `default` function and to catch it after
     * calling `applyOrElse` but I consider this somewhat inefficient.
     * <p>
     * I know only one way how you can do this task efficiently: `default` function should return unique marker object
     * which never may be returned by any other (regular/partial) function. This way after calling `applyOrElse` you need
     * just one reference comparison to distinguish if `pf isDefined x` or not.
     * <p>
     * This correctly interacts with specialization as return type of `applyOrElse`
     * (which is parameterized upper bound) can never be specialized.
     * <p>
     * Here `fallback_pf` is used as both unique marker object and special fallback function that returns it.
     */
    private static PartialFunction<Object, Object> fallback_pf = new PartialFunction<Object, Object>() {
        @Override
        public Boolean isDefinedAt(Object x) {
            return true;
        }

        @Override
        public Object apply(Object o) {
            return this;
        }
    };

    @SuppressWarnings("unchecked")
    public static <B> PartialFunction<Object, B> checkFallback() {
        return (PartialFunction<Object, B>) fallback_pf;
    }

    public static <B> Boolean fallbackOccurred(B x) {
        return fallback_pf.equals(x);
    }

    public static <T> Function<T, Boolean> constFalse() {
        return (x) -> false;
    }

    public static <A, B> PartialFunction<A, B> empty() {
        return new PartialFunction<A, B>() {
            @Override
            public Boolean isDefinedAt(A a) {
                return false;
            }

            @Override
            public B apply(A a) {
                throw new MatchError(a);
            }

            @Override
            @SuppressWarnings("unchecked")
            public PartialFunction<A, B> orElse(PartialFunction<? super A, ? extends B> that) {
                return (PartialFunction<A, B>) that;
            }

            @Override
            @SuppressWarnings("unchecked")
            public <V> PartialFunction<A, V> andThen(Function<? super B, ? extends V> after) {
                return (PartialFunction<A, V>) this;
            }

            @Override
            public Function<A, Optional<B>> lift() {
                return (x) -> Optional.empty();
            }

            @Override
            public <U> Function<A, Boolean> runWith(Function<? super B, ? extends U> action) {
                return constFalse();
            }
        };
    }

    public static <A, B> PartialFunction<A, B> unlifted(Function<A, Optional<B>> f) {
        if (f instanceof Lifted) {
            return ((Lifted<A, B>) f).pf;
        } else {
            return new Unlifted<>(f);
        }
    }

    /**
     * Composite function produced by `PartialFunction#orElse` method
     *
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     */
    public static class OrElse<A, B> extends AbstractPartialFunction<A, B> implements Serializable {

        private PartialFunction<A, B> f1;

        private PartialFunction<A, B> f2;

        public OrElse(PartialFunction<A, B> f1, PartialFunction<A, B> f2) {
            this.f1 = f1;
            this.f2 = f2;
        }

        @Override
        public Boolean isDefinedAt(A a) {
            return this.f1.isDefinedAt(a) || this.f2.isDefinedAt(a);
        }

        @Override
        public B apply(A a) {
            return this.f1.applyOrElse(a, this.f2);
        }

        @Override
        public B applyOrElse(A a, Function<? super A, ? extends B> zero) {
            B z = this.f1.applyOrElse(a, checkFallback());
            if (!fallbackOccurred(z)) {
                return z;
            } else {
                return this.f2.applyOrElse(a, zero);
            }
        }

        @Override
        public PartialFunction<A, B> orElse(PartialFunction<? super A, ? extends B> that) {
            return new OrElse<>(this.f1, f2.orElse(that));
        }

        @Override
        public <V> PartialFunction<A, V> andThen(Function<? super B, ? extends V> after) {
            return new OrElse<>(this.f1.andThen(after), this.f2.andThen(after));
        }
    }

    /**
     * Composite function produced by `PartialFunction#andThen` method
     *
     * @param <A> the type of the input to the function.
     * @param <B> the type of the result of the function.
     * @param <C> the type of output of the after function, and of the
     *            composed function
     */
    public static class AndThen<A, B, C> implements PartialFunction<A, C>, Serializable {

        private PartialFunction<A, B> pf;

        private Function<B, C> k;

        public AndThen(PartialFunction<A, B> pf, Function<B, C> k) {
            this.pf = pf;
            this.k = k;
        }

        @Override
        public Boolean isDefinedAt(A a) {
            return this.pf.isDefinedAt(a);
        }

        @Override
        public C apply(A a) {
            return this.k.apply(this.pf.apply(a));
        }

        @Override
        public C applyOrElse(A a, Function<? super A, ? extends C> zero) {
            B z = this.pf.applyOrElse(a, checkFallback());
            if (!fallbackOccurred(z)) {
                return this.k.apply(z);
            } else {
                return zero.apply(a);
            }
        }
    }

    public static class Lifted<A, B> implements Function<A, Optional<B>>, Serializable {

        private PartialFunction<A, B> pf;

        public Lifted(PartialFunction<A, B> pf) {
            this.pf = pf;
        }

        @Override
        public Optional<B> apply(A a) {
            B z = this.pf.applyOrElse(a, checkFallback());
            if (!fallbackOccurred(z)) {
                return Optional.of(z);
            } else {
                return Optional.empty();
            }
        }
    }

    public static class Unlifted<A, B> extends AbstractPartialFunction<A, B> implements Serializable {

        private Function<A, Optional<B>> f;

        public Unlifted(Function<A, Optional<B>> f) {
            this.f = f;
        }

        @Override
        public Boolean isDefinedAt(A a) {
            return f.apply(a).isPresent();
        }

        @Override
        public B applyOrElse(A a, Function<? super A, ? extends B> zero) {
            Optional<B> z = this.f.apply(a);
            return z.orElseGet(() -> zero.apply(a));
        }

        @Override
        public Function<A, Optional<B>> lift() {
            return this.f;
        }
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
        return pf.applyOrElse(x, constFalse());
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
        return pf.lift().apply(x);
    }

}
