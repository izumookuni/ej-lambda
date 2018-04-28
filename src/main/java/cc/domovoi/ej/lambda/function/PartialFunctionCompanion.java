package cc.domovoi.ej.lambda.function;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Function;

public class PartialFunctionCompanion {

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
        }
        else {
            return new Unlifted<>(f);
        }
    }

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
            }
            else {
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
            }
            else {
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
            }
            else {
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

    public static <T> Boolean cond(T x, PartialFunction<T, Boolean> pf) {
        return pf.applyOrElse(x, constFalse());
    }

    public static <T, U> Optional<U> condOpt(T x, PartialFunction<T, U> pf) {
        return pf.lift().apply(x);
    }

}
