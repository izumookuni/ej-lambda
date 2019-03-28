package cc.domovoi.collection.util;

public class Some<T> extends Option<T> {

    protected Some(T value) {
        super(value);
    }

    public static <T1> Some<T1> apply(T1 value) {
        if (value == null) {
            throw new IllegalArgumentException("value can not be null");
        }
        return new Some<>(value);
    }

    @Override
    public Boolean isEmpty() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("Some(%s)", this._value);
    }
}
