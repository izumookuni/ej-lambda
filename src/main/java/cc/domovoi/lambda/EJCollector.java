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


}
