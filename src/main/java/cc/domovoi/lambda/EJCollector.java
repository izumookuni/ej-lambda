package cc.domovoi.lambda;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class EJCollector {

    public static <T, K> Collector<T, ?, Map<K, Integer>> groupingByCountFilter(Function<? super T, ? extends K> classifier, Predicate<? super T> p) {
        return Collectors.groupingBy(classifier, Collectors.summingInt(t -> p.test(t) ? 1 : 0));
    }

    public static <T, K> Collector<T, ?, Map<K, Integer>> groupingByCountFilterNot(Function<? super T, ? extends K> classifier, Predicate<? super T> p) {
        return groupingByCountFilter(classifier, p.negate());
    }

    public static <T, K> Collector<T, ?, Map<K, Integer>> groupingByCount(Function<? super T, ? extends K> classifier) {
        return groupingByCountFilter(classifier, EJLambda.predicateTrue());
    }

    public static <T> Collector<T, ?, Integer> countingInt() {
        return Collectors.summingInt(EJLambda.toIntValueOne());
    }

    public static <T> Collector<T, ?, Long> countingLong() {
        return Collectors.summingLong(EJLambda.toLongValueOne());
    }

    public static <T, K1, K2, A, D> Collector<T, ?, Map<K1, Map<K2, D>>> groupingTwiceBy(Function<? super T, ? extends K1> classifier1,
                                                                                         Function<? super T, ? extends K2> classifier2,
                                                                                         Collector<? super T, A, D> downstream) {
        return Collectors.groupingBy(classifier1, Collectors.groupingBy(classifier2, downstream));
    }

    public static <T, K1, K2, K3, A, D> Collector<T, ?, Map<K1, Map<K2, Map<K3, D>>>> groupingThriceBy(Function<? super T, ? extends K1> classifier1,
                                                                                                       Function<? super T, ? extends K2> classifier2,
                                                                                                       Function<? super T, ? extends K3> classifier3,
                                                                                                       Collector<? super T, A, D> downstream) {
        return Collectors.groupingBy(classifier1, Collectors.groupingBy(classifier2, Collectors.groupingBy(classifier3, downstream)));
    }

}
