package cc.domovoi.collection.util;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class Option<T> extends Product implements Serializable {

    protected final T _value;

    protected Option(T value) {
        this._value = value;
    }

    public abstract Boolean isEmpty();

    public static <T1> Option<T1> from(Optional<T1> elem) {
        if (elem.isPresent()) {
            return Some.apply(elem.get());
        }
        else {
            return None.unit();
        }
    }

    public T get() {
        if (!isEmpty()) {
            return this._value;
        }
        else {
            throw new NoSuchElementException("No value present");
        }
    }

    public Boolean contains(T elem) {
        return !isEmpty() && ((elem != null && elem.equals(this._value)) || this._value == null);
    }

    public Boolean exist(Predicate<? super T> p) {
        return !isEmpty() && p.test(this._value);
    }

    public Option<T> filter(Predicate<? super T> p) {
        if (!isEmpty() && !p.test(this._value)) {
            return None.unit();
        }
        else {
            return this;
        }
    }

    public Option<T> filterNot(Predicate<? super T> p) {
        return filter(p.negate());
    }

    public <U> Option<U> flatMap(Function<? super T, ? extends Option<U>> op) {
        if (!isEmpty()) {
            return op.apply(this._value);
        }
        else {
            return None.unit();
        }
    }

    public static <T1> Option<T1> flatten(Option<Option<T1>> option) {
        return option.flatMap(Function.identity());
    }

    public <U> U fold(U zero, Function<? super T, ? extends U> op) {
        if (!isEmpty()) {
            return op.apply(this._value);
        }
        else {
            return zero;
        }
    }

    public Boolean forall(Predicate<? super T> p) {
        return (!isEmpty() && p.test(this._value)) || isEmpty();
    }

    public void foreach(Consumer<? super T> op) {
        if (!isEmpty()) {
            op.accept(this._value);
        }
    }

    public T getOrElse(Supplier<? extends T> zero) {
        if (!isEmpty()) {
            return this._value;
        }
        else {
            return zero.get();
        }
    }

    public <U> Option<U> map(Function<? super T, ? extends U> op) {
        return flatMap(op.andThen(Some::apply));
    }

    public Boolean nonEmpty() {
        return !isEmpty();
    }

    public Option<T> orElse(Supplier<? extends Option<T>> zero) {
        if (!isEmpty()) {
            return this;
        }
        else {
            return zero.get();
        }
    }

    public T orNull() {
        if (!isEmpty()) {
            return this._value;
        }
        else {
            return null;
        }
    }

    public Optional<T> toOptional() {
        if (!isEmpty()) {
            return Optional.of(this._value);
        }
        else {
            return Optional.empty();
        }
    }

    public <U> Either<T, U> toLeft(Supplier<? extends U> right) {
        if (!isEmpty()) {
            return new Left<>(this._value);
        }
        else {
            return new Right<>(right.get());
        }
    }

    public <U> Either<U, T> toRight(Supplier<? extends U> left) {
        if (!isEmpty()) {
            return new Right<>(this._value);
        }
        else {
            return new Left<>(left.get());
        }
    }

    public List<T> toList() {
        if (!isEmpty()) {
            return Collections.singletonList(this._value);
        }
        else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Option<?> option = (Option<?>) o;
        return _value.equals(option._value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_value);
    }

    @Override
    public List<Object> productCollection() {
        if(!isEmpty()) {
            return Collections.singletonList(this._value);
        }
        else {
            return Collections.emptyList();
        }
    }
}
