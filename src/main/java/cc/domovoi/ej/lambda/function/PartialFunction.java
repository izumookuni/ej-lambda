package cc.domovoi.ej.lambda.function;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface PartialFunction<A, B> extends Function<A, B>, EJFunction<A, B> {

    Boolean isDefinedAt(A a);

    @SuppressWarnings("unchecked")
    default PartialFunction<A, B> orElse(PartialFunction<? super A, ? extends B> that) {
        return new PartialFunctionCompanion.OrElse<>(this, (PartialFunction<A, B>) that);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <V> PartialFunction<A, V> andThen(Function<? super B, ? extends V> after) {
        return new PartialFunctionCompanion.AndThen<>(this, (PartialFunction<B, V>) after);
    }

    @Override
    @SuppressWarnings("unchecked")
    default <V> PartialFunction<V, B> compose(Function<? super V, ? extends A> before) {
        return new PartialFunctionCompanion.AndThen<>((PartialFunction<V, A>) before, this);
    }

    default Function<A, Optional<B>> lift() {
        return new PartialFunctionCompanion.Lifted<>(this);
    }

    default B applyOrElse(A a, Function<? super A, ? extends B> zero) {
        if (isDefinedAt(a)) {
            return apply(a);
        }
        else {
            return zero.apply(a);
        }
    }

    default PartialFunction<A, B> end() {
        return this.orElse(PartialFunctionCompanion.empty());
    }

    default <U> Function<A, Boolean> runWith(Function<? super B, ? extends U> action) {
        return (x) -> {
            B z = applyOrElse(x, PartialFunctionCompanion.checkFallback());
            if (!PartialFunctionCompanion.fallbackOccurred(z)) {
                action.apply(z);
                return true;
            }
            else {
                return false;
            }
        };
    }

    default PartialFunction<A, B> orElseOf(Predicate<A> isDefinedAt, Function<A, B> apply) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.of(isDefinedAt, apply));
    }

    default PartialFunction<A, B> orElseFrom(Function<A, Optional<B>> f) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.from(f));
    }

    default <V> PartialFunction<A, V> andThenOf(Predicate<B> isDefinedAt, Function<B, V> apply) {
        return new PartialFunctionCompanion.AndThen<>(this, PartialFunctions.of(isDefinedAt, apply));
    }

    default <V> PartialFunction<A, V> andThenFrom(Function<B, Optional<V>> f) {
        return new PartialFunctionCompanion.AndThen<>(this, PartialFunctions.from(f));
    }

    default PartialFunction<A, B> orEndWith(B b) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.of(a -> true, a -> b));
    }

    default PartialFunction<A, B> orDefault(Supplier<B> f) {
        return new PartialFunctionCompanion.OrElse<>(this, PartialFunctions.of(a -> true, a -> f.get()));
    }
}
