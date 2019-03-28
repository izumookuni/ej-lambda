package cc.domovoi.lambda;

import java.util.function.Function;
import java.util.function.Predicate;

public class EJLambda {

    private static short SHORT_ONE = 1;

    private static byte BYTE_ONE = 1;

    public static <T> Predicate<T> predicateFalse() {
        return (t) -> false;
    }

    public static <T> Predicate<T> predicateTrue() {
        return (t) -> true;
    }

    public static <T> Function<T, Integer> toIntOne() {
        return (t) -> 1;
    }

    public static <T> Function<T, Double> toDoubleOne() {
        return (t) -> 1.0;
    }

    public static <T> Function<T, Long> toLongOne() {
        return (t) -> 1L;
    }

    public static <T> Function<T, Short> toShortOne() {
        return (t) -> SHORT_ONE;
    }

    public static <T> Function<T, Byte> toByteOne() {
        return (t) -> BYTE_ONE;
    }

}
