package cc.domovoi.collection.util;

/**
 * The right side of the disjoint union, as opposed to the {@link cc.domovoi.collection.util.Left} side.
 *
 * @param <L> Left type of this Either instance
 * @param <R> Right type of this Either instance
 */
public final class Right<L, R> extends Either<L, R> {

    public Right(R right) {
        super();
        this._left = null;
        this._right = right;
    }

    public R get() {
        return this._right;
    }

    public static <L1, R1> Right<L1, R1> apply(R1 right) {
        return new Right<>(right);
    }

    @Override
    public Boolean isLeft() {
        return false;
    }

    @Override
    public Boolean isRight() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("Right(%s)", this._right);
    }
}
