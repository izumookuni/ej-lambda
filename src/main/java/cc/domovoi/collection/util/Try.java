package cc.domovoi.collection.util;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The `Try` type represents a computation that may either result in an exception, or return a
 * successfully computed value. It's similar to, but semantically different from the {@link cc.domovoi.collection.util.Either} type.
 * <p>
 * Instances of `Try&lt;T&gt;`;, are either an instance of {@link cc.domovoi.collection.util.Success}&lt;T&gt; or {@link cc.domovoi.collection.util.Failure}&lt;T&gt;.
 *
 * @param <T> Element type of this Try
 */
public abstract class Try<T> extends Product implements Serializable {

    protected T _value;

    protected Throwable _exception;

    /**
     * Returns `true` if the `Try` is a `Failure`, `false` otherwise.
     *
     * @return Returns `true` if the `Try` is a `Failure`, `false` otherwise.
     */
    public abstract Boolean isFailure();

    /**
     * Returns `true` if the `Try` is a `Success`, `false` otherwise.
     *
     * @return Returns `true` if the `Try` is a `Success`, `false` otherwise.
     */
    public abstract Boolean isSuccess();

    /**
     * Constructs a `Try` using the by-name parameter.  This
     * method will ensure any non-fatal exception is caught and a
     * `Failure` object is returned.
     *
     * @param supplier the element supplier
     * @param <T1>     element type of this Try
     * @return a Try instance
     */
    public static <T1> Try<T1> apply(Supplier<T1> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable failure) {
            return new Failure<>(failure);
        }
    }

    /**
     * Inverts this `Try`. If this is a `Failure`, returns its exception wrapped in a `Success`.
     * If this is a `Success`, returns a `Failure` containing an `UnsupportedOperationException`.
     *
     * @return If this is a `Failure`, returns its exception wrapped in a `Success`;
     * If this is a `Success`, returns a `Failure` containing an `UnsupportedOperationException`.
     */
    public Try<Throwable> failed() {
        if (isFailure()) {
            return new Success<>(this._exception);
        } else {
            return new Failure<>(new NoSuchElementException("This object isn't instance of Failure"));
        }
    }

    /**
     * Converts this to a `Failure` if the predicate is not satisfied.
     *
     * @param p Predicate function
     * @return `Failure` if the predicate is not satisfied, or this.
     */
    public Try<T> filter(Predicate<? super T> p) {
        if (isSuccess() && !p.test(this._value)) {
            return new Failure<>(new AssertionError("Predicate Failure"));
        } else {
            return this;
        }
    }

    /**
     * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     *
     * @param f   the given function
     * @param <U> the result type of applying the function
     * @return Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     */
    public <U> Try<U> flatMap(Function<? super T, ? extends Try<U>> f) {
        if (isSuccess()) {
            return f.apply(this._value);
        } else {
            return new Failure<>(this._exception);
        }
    }

    /**
     * Transforms a nested `Try`, ie, a `Try` of type `Try&lt;Try&lt;T&gt;&gt;`,
     * into an un-nested `Try`, ie, a `Try` of type `Try&lt;T&gt;`.
     *
     * @param t   A `Try` instance
     * @param <U> Element type of this Try
     * @return A flatten `Try` instance
     */
    public static <U> Try<U> flatten(Try<Try<U>> t) {
        return t.flatMap(Function.identity());
    }

    /**
     * Applies `fa` if this is a `Failure` or `fb` if this is a `Success`.
     * If `fb` is initially applied and throws an exception,
     * then `fa` is applied with this exception.
     *
     * @param fa  the function to apply if this is a `Failure`
     * @param fb  the function to apply if this is a `Success`
     * @param <U> the result type of applying the function
     * @return the results of applying the function
     */
    public <U> U fold(Function<? super Throwable, ? extends U> fa, Function<? super T, ? extends U> fb) {
        if (isSuccess()) {
            return fb.apply(this._value);
        } else {
            return fa.apply(this._exception);
        }
    }

    /**
     * Applies the given function `f` if this is a `Success`, otherwise returns `Unit` if this is a `Failure`.
     * <p>
     * If `f` throws, then this method may throw an exception.
     *
     * @param f The side-effecting function to execute.
     */
    public void foreach(Consumer<? super T> f) {
        if (isSuccess()) {
            f.accept(this._value);
        }
    }

    /**
     * Returns the value from this `Success` or throws the exception if this is a `Failure`.
     *
     * @return Returns the value from this `Success` or throws the exception if this is a `Failure`.
     */
    public T get() {
        if (isSuccess()) {
            return this._value;
        } else {
            throw new NoSuchElementException("This object isn't instance of Success");
        }
    }

    /**
     * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
     * <p>
     * This will throw an exception if it is not a success and default throws an exception.
     *
     * @param zero the default argument
     * @return Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
     */
    public T getOrElse(Supplier<? extends T> zero) {
        if (isSuccess()) {
            return this._value;
        } else {
            return zero.get();
        }
    }

    public T getOrElse(T zero) {
        if (isSuccess()) {
            return this._value;
        } else {
            return zero;
        }
    }

    /**
     * Maps the given function to the value from this `Success` or returns this if this is a `Failure`.
     *
     * @param f   the given function
     * @param <U> the result type of applying the function
     * @return Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     */
    public <U> Try<U> map(Function<? super T, ? extends U> f) {
        if (isSuccess()) {
            return Try.apply(() -> f.apply(this._value));
//            return new Success<>(f.apply(this._value));
        } else {
            return new Failure<>(this._exception);
        }
    }

    /**
     * Returns this `Try` if it's a `Success` or the given `default` argument if this is a `Failure`.
     *
     * @param zero the given `default` argument
     * @return Returns this `Try` if it's a `Success` or the given `default` argument if this is a `Failure`.
     */
    public Try<T> orElse(Supplier<? extends Try<T>> zero) {
        if (isSuccess()) {
            return this;
        } else {
            return zero.get();
        }
    }

    /**
     * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like map for the exception.
     *
     * @param f the given recover function
     * @return Returns this if this is a `Success`, or a new `Try` instance applies the given function `f`.
     */
    public Try<T> recover(Function<? super Throwable, ? extends T> f) {
        if (isFailure()) {
            return Try.apply(() -> f.apply(this._exception));
        } else {
            return this;
        }
    }

    /**
     * Applies the given function `f` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like `flatMap` for the exception.
     *
     * @param f the given recover function
     * @return Returns this if this is a `Success`, or a new `Try` instance applies the given function `f`.
     */
    public Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> f) {
        if (isFailure()) {
            return f.apply(this._exception);
        } else {
            return this;
        }
    }

    /**
     * Returns `Left` with `Throwable` if this is a `Failure`, otherwise returns `Right` with `Success` value.
     *
     * @return Returns `Left` with `Throwable` if this is a `Failure`, otherwise returns `Right` with `Success` value.
     */
    public Either<Throwable, T> toEither() {
        if (isSuccess()) {
            return new Right<>(this._value);
        } else {
            return new Left<>(this._exception);
        }
    }

    /**
     * Returns `None` if this is a `Failure` or a `Some` containing the value if this is a `Success`.
     *
     * @return Returns `None` if this is a `Failure` or a `Some` containing the value if this is a `Success`.
     */
    public Optional<T> toOptional() {
        if (isSuccess()) {
            return Optional.of(this._value);
        } else {
            return Optional.empty();
        }
    }

    public Option<T> toOption() {
        if (isSuccess()) {
            return Some.apply(this._value);
        }
        else {
            return None.unit();
        }
    }

    /**
     * Completes this `Try` by applying the function `f` to this if this is of type `Failure`, or conversely, by applying
     * `s` if this is a `Success`.
     *
     * @param s   the function to apply if this is a `Failure`
     * @param f   the function to apply if this is a `Success`
     * @param <U> the result type of applying the function
     * @return the results of applying the function
     */
    public <U> Try<U> transform(Function<? super T, ? extends Try<U>> s, Function<? super Throwable, ? extends Try<U>> f) {
        if (isSuccess()) {
            return s.apply(this._value);
        } else {
            return f.apply(this._exception);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Try<?> aTry = (Try<?>) o;
        return Objects.equals(_value, aTry._value) &&
                Objects.equals(_exception, aTry._exception);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_value, _exception);
    }

    @Override
    public Integer productArity() {
        if (isFailure()) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public Object productElement(Integer n) {
        if (isSuccess() && n == 0) {
            return this._value;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public List<Object> productCollection() {
        if (isSuccess()) {
            return Collections.singletonList(this._value);
        } else {
            return Collections.emptyList();
        }
    }
}
