package cc.domovoi.ej.lambda.function;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class PartialFunctions {

    public static <A, B> PartialFunction<A, B> empty() {
        return PartialFunctionCompanion.empty();
    }

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

    public static <A, B> PartialFunction<A, B> from(Function<A, Optional<B>> f) {
        return unlift(f);
    }

    public static <A, B> PartialFunction<A, B> fromAll(Collection<Function<A, Optional<B>>> fs) {
        return fs.stream().map(PartialFunctions::from).reduce(empty(), PartialFunction::orElse);
    }

    public static <A, B> PartialFunction<A, B> withEnd(Predicate<A> isDefinedAt, Function<A, B> apply) {
        return of(isDefinedAt, apply).end();
    }

    public static <A, B> PartialFunction<A, B> fromWithEnd(Function<A, Optional<B>> f) {
        return from(f).end();
    }

    public static <A, B> PartialFunction<A, B> fromAllWithEnd(Collection<Function<A, Optional<B>>> fs) {
        return fromAll(fs).end();
    }

    public static <A, B> Function<A, Optional<B>> lift(PartialFunction<A, B> pf) {
        return pf.lift();
    }

    public static <A, B> PartialFunction<A, B> unlift(Function<A, Optional<B>> f) {
        return PartialFunctionCompanion.unlifted(f);
    }

    public static <T> Boolean cond(T x, PartialFunction<T, Boolean> pf) {
        return PartialFunctionCompanion.cond(x, pf);
    }

    public static <T, U> Optional<U> condOpt(T x, PartialFunction<T, U> pf) {
        return PartialFunctionCompanion.condOpt(x, pf);
    }
}
