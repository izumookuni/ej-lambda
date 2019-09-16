package cc.domovoi.lambda.test;

import cc.domovoi.lambda.EJCollector;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EJCollectorTest {

    public class ClassA {
        private String v1;

        private String v2;

        public ClassA(String v1, String v2) {
            this.v1 = v1;
            this.v2 = v2;
        }

        public String getV1() {
            return v1;
        }

        public void setV1(String v1) {
            this.v1 = v1;
        }

        public String getV2() {
            return v2;
        }

        public void setV2(String v2) {
            this.v2 = v2;
        }
    }

    @Test
    public void testCollectDualMap() {
        List<ClassA> classAList = Arrays.asList(new ClassA("a", "b"), new ClassA("a", "c"), new ClassA("b", "b"), new ClassA("a", "b"));
        Map<String, Map<String, Integer>> dualMap = classAList.stream()
                .collect(Collectors.groupingBy(ClassA::getV1, Collectors.collectingAndThen(Collectors.groupingBy(ClassA::getV2, EJCollector.countingInt()), Function.identity())));

        Map<String, Map<String, Integer>> dualMap2 = classAList.stream()
                .collect(Collectors.groupingBy(ClassA::getV1, Collectors.groupingBy(ClassA::getV2, EJCollector.countingInt())));

        Map<String, Map<String, Integer>> dualMap3 = classAList.stream()
                .collect(EJCollector.groupingTwiceBy(ClassA::getV1, ClassA::getV2, EJCollector.countingInt()));

        dualMap.forEach((k1, v1) -> v1.forEach((k2, v2) -> System.out.println(String.format("(%s,%s) -> %s", k1, k2, v2))));
        System.out.println("====");
        dualMap2.forEach((k1, v1) -> v1.forEach((k2, v2) -> System.out.println(String.format("(%s,%s) -> %s", k1, k2, v2))));
        System.out.println("====");
        dualMap3.forEach((k1, v1) -> v1.forEach((k2, v2) -> System.out.println(String.format("(%s,%s) -> %s", k1, k2, v2))));
    }
}
