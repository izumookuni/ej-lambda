package cc.domovoi.lambda.test;

import cc.domovoi.lambda.function.MatchError;
import cc.domovoi.lambda.function.PartialFunction;
import cc.domovoi.lambda.function.PartialFunctions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PartialFunctionTest {

    @Test
    public void testPartialFunctionOf() {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        PartialFunction<Integer, String> partialFunction =
                PartialFunctions.of((Integer integer) -> integer < 3, integer -> "elem less than 3")
                        .orElseOf(integer -> integer >= 3, integer -> "elem larger than / equal 3")
                        .end();
        List<String> stringList = integerList.stream().map(partialFunction).collect(Collectors.toList());
        Assert.assertTrue(stringList.stream().limit(2).allMatch("elem less than 3"::equals));
        Assert.assertTrue(stringList.stream().skip(2).allMatch("elem larger than / equal 3"::equals));
    }

    @Test
    public void testPartialFunctionFrom() {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        Function<Integer, Optional<String>> function1 = integer -> integer < 3 ? Optional.of("elem less than 3") : Optional.empty();
        Function<Integer, Optional<String>> function2 = integer -> integer >= 3 ? Optional.of("elem larger than / equal 3") : Optional.empty();
        PartialFunction<Integer, String> partialFunction = PartialFunctions.from(function1).orElseFrom(function2).end();
        List<String> stringList = integerList.stream().map(partialFunction).collect(Collectors.toList());
        Assert.assertTrue(stringList.stream().limit(2).allMatch("elem less than 3"::equals));
        Assert.assertTrue(stringList.stream().skip(2).allMatch("elem larger than / equal 3"::equals));
    }

    @Test
    public void testPartialFunctionEndWith() {
        List<Integer> integerList = Arrays.asList(1, 2, 3, 4, 5);
        List<String> stringList = integerList.stream()
                .map(
                        PartialFunctions.of((Integer integer) -> integer < 3, integer -> "elem less than 3")
                                .orElseOf(integer -> integer > 3, integer -> "elem larger than 3")
                                .orEndWith("elem equal 3")
                ).collect(Collectors.toList());
        Assert.assertTrue(stringList.stream().limit(2).allMatch("elem less than 3"::equals));
        Assert.assertTrue("elem equal 3".equals(stringList.get(2)));
        Assert.assertTrue(stringList.stream().skip(3).allMatch("elem larger than 3"::equals));
    }

    @Test
    public void testPartialFunctionNoDefined() {
        PartialFunction<Integer, String> partialFunction = PartialFunctions.of((Integer integer) -> integer > 0, integer -> "elem is a positive number").orElseOf(integer -> integer < 0, integer -> "elem is a negative number").end();
        Assert.assertTrue("elem is a positive number".equals(partialFunction.apply(1)));
        Assert.assertTrue("elem is a negative number".equals(partialFunction.apply(-1)));
        try {
            partialFunction.apply(0);
        } catch (MatchError e) {
            // Nothing
        }
    }

    @Test
    public void testPartialFunctionAndThen() {
        PartialFunction<Integer, Integer> partialFunction1 = PartialFunctions.of((Integer integer) -> integer > 0, integer -> 1).orElseOf(integer -> integer < 0, integer -> -1).end();
        PartialFunction<Integer, String> partialFunction2 = PartialFunctions.of((Integer integer) -> integer == 1, integer -> "elem is 1").orElseOf(integer -> integer == -1, integer -> "elem is -1").end();
        PartialFunction<Integer, String> partialFunction3 = partialFunction1.andThen(partialFunction2);
        Assert.assertTrue("elem is 1".equals(partialFunction3.apply(42)));
        Assert.assertTrue("elem is -1".equals(partialFunction3.apply(-42)));
    }

    @Test
    public void testPartialFunctionCompose() {
        PartialFunction<Integer, Integer> partialFunction1 = PartialFunctions.of((Integer integer) -> integer > 0, integer -> 1).orElseOf(integer -> integer < 0, integer -> -1).end();
        PartialFunction<Integer, String> partialFunction2 = PartialFunctions.of((Integer integer) -> integer == 1, integer -> "elem is 1").orElseOf(integer -> integer == -1, integer -> "elem is -1").end();
        PartialFunction<Integer, String> partialFunction3 = partialFunction2.compose(partialFunction1);
        Assert.assertTrue("elem is 1".equals(partialFunction3.apply(42)));
        Assert.assertTrue("elem is -1".equals(partialFunction3.apply(-42)));
    }
}
