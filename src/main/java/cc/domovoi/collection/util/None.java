package cc.domovoi.collection.util;

public class None<T> extends Option<T> {

    protected None() {
        super(null);
    }

    public static <T1> None<T1> unit() {
        return new None<>();
    }

    @Override
    public Boolean isEmpty() {
        return true;
    }

    @Override
    public String toString() {
        return "None";
    }
}
