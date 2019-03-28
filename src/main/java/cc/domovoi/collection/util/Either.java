package cc.domovoi.collection.util;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Represents a value of one of two possible types (a disjoint union.)
 * An instance of `Either` is an instance of either {@link cc.domovoi.collection.util.Left} or {@link cc.domovoi.collection.util.Right}.
 *
 * @param <L> Left type of this Either instance
 * @param <R> Right type of this Either instance
 */
public abstract class Either<L, R> extends Product implements Serializable {

    protected L _left;

    protected R _right;

    /**
     * Returns `true` if this is a `Left`, `false` otherwise.
     *
     * @return Returns `true` if this is a `Left`, `false` otherwise.
     */
    public abstract Boolean isLeft();

    /**
     * Returns `true` if this is a `Right`, `false` otherwise.
     *
     * @return Returns `true` if this is a `Right`, `false` otherwise.
     */
    public abstract Boolean isRight();

    /**
     * Returns `true` if this is a `Right` and its value is equal to `elem` (as determined by `equals`),
     * returns `false` otherwise.
     *
     * @param r the element to test.
     * @return `true` if this is a `Right` value equal to `r`.
     */
    public Boolean contains(R r) {
        return isRight() && r.equals(this._right);
    }

    /**
     * Returns `false` if `Left` or returns the result of the application of
     * the given predicate to the `Right` value.
     *
     * @param p Predicate function
     * @return Returns `false` if `Left` or returns the result of the application of the given predicate to the `Right` value.
     */
    public Boolean exists(Predicate<? super R> p) {
        return isRight() && p.test(this._right);
    }

    /**
     * Returns `Right` with the existing value of `Right` if this is a `Right`
     * and the given predicate `p` holds for the right value,
     * or `Left(zero)` if this is a `Right` and the given predicate `p` does not hold for the right value,
     * or `Left` with the existing value of `Left` if this is a `Left`.
     *
     * @param p    Predicate function
     * @param zero the default value supplier.
     * @return Returns `Right` with the existing value of `Right` if this is a `Right`
     * and the given predicate `p` holds for the right value,
     * or `Left(zero)` if this is a `Right` and the given predicate `p` does not hold for the right value,
     * or `Left` with the existing value of `Left` if this is a `Left`.
     */
    public Either<L, R> filterOrElse(Predicate<? super R> p, Supplier<? extends L> zero) {
        if ((isRight() && p.test(_right)) || isLeft()) {
            return this;
        } else {
            return new Left<>(zero.get());
        }
    }

    /**
     * Binds the given function across `Right`.
     *
     * @param f    The function to bind across `Right`.
     * @param <R1> the result type of applying the function
     * @return An new Either instance with `R1` right type
     */
    public <R1> Either<L, R1> flatMap(Function<? super R, ? extends Either<L, R1>> f) {
        if (isRight()) {
            return f.apply(this._right);
        } else {
            return new Left<>(this._left);
        }
    }

    /**
     * Applies `fa` if this is a `Left` or `fb` if this is a `Right`.
     *
     * @param fl  the function to apply if this is a `Left`
     * @param fr  the function to apply if this is a `Right`
     * @param <T> the result type of applying the function
     * @return the results of applying the function
     */
    public <T> T fold(Function<? super L, ? extends T> fl, Function<? super R, ? extends T> fr) {
        if (isRight()) {
            return fr.apply(this._right);
        } else {
            return fl.apply(this._left);
        }
    }

    /**
     * Returns `true` if `Left` or returns the result of the application of
     * the given predicate to the `Right` value.
     *
     * @param p Predicate function
     * @return Returns `true` if `Left` or returns the result of the application of the given predicate to the `Right` value.
     */
    public Boolean forall(Predicate<? super R> p) {
        return (isRight() && p.test(this._right)) || isLeft();
    }

    /**
     * Executes the given side-effecting function if this is a `Right`.
     *
     * @param f The side-effecting function to execute.
     */
    public void foreach(Consumer<? super R> f) {
        if (isRight()) {
            f.accept(this._right);
        }
    }

    /**
     * Returns the value from this `Right` or the given argument if this is a `Left`.
     *
     * @param zero the default value.
     * @return Returns the value from this `Right` or the given argument if this is a `Left`.
     */
    public R getOrElse(Supplier<? extends R> zero) {
        if (isRight()) {
            return this._right;
        } else {
            return zero.get();
        }
    }

    /**
     * Joins an `Either` through `Left`.
     *
     * @param either An Either instance
     * @param <L1>   Left type of this Either instance
     * @param <R1>   Right type of this Either instance
     * @return An joined Either instance
     */
    public static <L1, R1> Either<L1, R1> joinLeft(Either<Either<L1, R1>, R1> either) {
        if (either.isLeft() && either._left.isLeft()) {
            return new Left<>(either._left._left);
        } else if (either.isLeft() && either._left.isRight()) {
            return new Right<>(either._left._right);
        } else {
            return new Right<>(either._right);
        }
    }

    /**
     * Joins an `Either` through `Right`.
     * This method requires that the right side of this `Either` is itself
     * an `Either` type.
     *
     * @param either An either instance
     * @param <L1>   Left type of this Either instance
     * @param <R1>   Right type of this Either instance
     * @return An joined either instance
     */
    public static <L1, R1> Either<L1, R1> joinRight(Either<L1, Either<L1, R1>> either) {
        if (either.isRight() && either._right.isLeft()) {
            return new Left<>(either._right._left);
        } else if (either.isRight() && either._right.isRight()) {
            return new Right<>(either._right._right);
        } else {
            return new Left<>(either._left);
        }
    }

    /**
     * Projects this `Either` as a `Left`.
     *
     * @return Projects this `Either` as a `Left`.
     */
    public Left<L, R> left() {
        if (isLeft()) {
            return (Left<L, R>) this;
        } else {
            throw new ClassCastException("This object isn't instance of Left");
        }
    }

    /**
     * Projects this `Either` as a `Right`.
     *
     * @return Projects this `Either` as a `Right`.
     */
    public Right<L, R> right() {
        if (isRight()) {
            return (Right<L, R>) this;
        } else {
            throw new ClassCastException("This object isn't instance of Right");
        }
    }

    /**
     * The given function is applied if this is a `Right`.
     *
     * @param f   The function to bind across `Right`.
     * @param <T> the result type of applying the function
     * @return An new Either instance with `R1` right type
     */
    public <T> Either<L, T> map(Function<? super R, ? extends T> f) {
        if (isRight()) {
            return new Right<>(f.apply(this._right));
        } else {
            return new Left<>(this._left);
        }
    }

    /**
     * If this is a `Left`, then return the left value in `Right` or vice versa.
     *
     * @return If this is a `Left`, then return the left value in `Right` or vice versa.
     */
    public Either<R, L> swap() {
        if (isLeft()) {
            return new Right<>(this._left);
        } else {
            return new Left<>(this._right);
        }
    }

    /**
     * Returns a `Some` containing the `Right` value
     * if it exists or a `None` if this is a `Left`.
     *
     * @return Returns a `Some` containing the `Right` value
     * if it exists or a `None` if this is a `Left`.
     */
    public Optional<R> toOptional() {
        if (isRight()) {
            return Optional.of(this._right);
        } else {
            return Optional.empty();
        }
    }

    public Option<R> toOption() {
        if (isRight()) {
            return Some.apply(this._right);
        }
        else {
            return None.unit();
        }
    }

    /**
     * Returns a `Seq` containing the `Right` value if
     * it exists or an empty `Seq` if this is a `Left`.
     *
     * @return Returns a `Seq` containing the `Right` value if
     * it exists or an empty `Seq` if this is a `Left`.
     */
    public List<R> toList() {
        if (isRight()) {
            return Collections.singletonList(this._right);
        } else {
            return Collections.emptyList();
        }
    }

    public Try<R> toTry() {
        if (isRight()) {
            return new Success<>(this._right);
        } else {
            return new Failure<>(new ClassCastException("This object isn't instance of Right"));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Either<?, ?> either = (Either<?, ?>) o;
        return Objects.equals(_left, either._left) &&
                Objects.equals(_right, either._right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_left, _right);
    }

    @Override
    public Integer productArity() {
        return 1;
    }

    @Override
    public Object productElement(Integer n) {
        if (n == 0) {
            if (isRight()) {
                return _right;
            } else {
                return _left;
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public List<Object> productCollection() {
        return Arrays.asList(this._left, this._right);
    }
}
