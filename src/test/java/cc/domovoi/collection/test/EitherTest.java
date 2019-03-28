package cc.domovoi.collection.test;


import cc.domovoi.collection.util.Either;
import cc.domovoi.collection.util.Left;
import cc.domovoi.collection.util.Right;
import cc.domovoi.collection.util.Try;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class EitherTest {

    private Either<String, Double> either1 = new Right<>(3.14);
    private Either<String, Double> either2 = new Left<>("hello");

    @Test
    public void testConstructor() {
        Assert.assertTrue(either1.isRight() && either1.right().get() == 3.14);
        Assert.assertTrue(either2.isLeft() && "hello".equals(either2.left().get()));
    }

    @Test
    public void testContains() {
        Assert.assertTrue(either1.contains(3.14));
        Assert.assertFalse(either2.contains(3.14));
//        Assert.assertFalse(either2.contains("hello"));
    }

    @Test
    public void testeExists() {
        Assert.assertTrue(either1.exists(d -> d == 3.14));
        Assert.assertTrue(either1.exists(d -> d > 3));
        Assert.assertTrue(either1.exists(d -> d < Math.PI));
        Assert.assertFalse(either2.exists(d -> d == 3.14));
    }

    @Test
    public void testFilterOrElse() {
        Either<String, Double> either3 = either1.filterOrElse(d -> d < Math.PI, () -> "error1");
        Either<String, Double> either4 = either1.filterOrElse(d -> d < 1, () -> "error2");
        Either<String, Double> either5 = either2.filterOrElse(p -> p == 3.14, () -> "error3");
        Assert.assertTrue(either3.isRight() && either3.right().get() == 3.14);
        Assert.assertTrue(either4.isLeft() && "error2".equals(either4.left().get()));
        Assert.assertTrue(either5.isLeft() && "hello".equals(either5.left().get()));
    }

    @Test
    public void testMapAndFlatMap() {
        Either<String, Double> either3 = new Right<>(10.0);
        Either<String, Double> either4 = new Right<>(1.0);
        Either<String, Double> either5 = either1.flatMap(d1 -> either3.flatMap(d2 -> either4.map(d3 -> d1 + d2 + d3)));
        Either<String, Double> either6 = either2.flatMap(d1 -> either3.flatMap(d2 -> either4.map(d3 -> d1 + d2 + d3)));
        Assert.assertTrue(either5.isRight() && either5.right().get() == 14.14);
        Assert.assertTrue(either6.isLeft() && "hello".equals(either6.left().get()));
    }

    @Test
    public void testFold() {
        Integer i1 = either1.fold(s -> 65536, Double::intValue);
        Integer i2 = either2.fold(s -> 65536, Double::intValue);
        Assert.assertTrue(i1 == 3);
        Assert.assertTrue(i2 == 65536);
    }

    @Test
    public void testForall() {
        Assert.assertTrue(either1.forall(d -> d == 3.14));
        Assert.assertTrue(either1.forall(d -> d > 3));
        Assert.assertTrue(either1.forall(d -> d < Math.PI));
        Assert.assertTrue(either2.forall(d -> d == 3.14));
        Assert.assertTrue(either2.forall(d -> d > 3));
        Assert.assertTrue(either2.forall(d -> d < Math.PI));
    }

    @Test
    public void testForeach() {
        either1.foreach(d -> Assert.assertTrue(d == 3.14));
        either2.foreach(d -> {
            throw new RuntimeException("error");
        });
    }

    @Test
    public void testGetOrElse() {
        Double d1 = either1.getOrElse(() -> 42.0);
        Double d2 = either2.getOrElse(() -> 42.0);
        Assert.assertTrue(d1 == 3.14);
        Assert.assertTrue(d2 == 42);
    }

    @Test
    public void testJoinLeft() {
        Either<Either<String, Double>, Double> either3 = new Right<>(1.0);
        Either<Either<String, Double>, Double> either4 = new Left<>(new Left<>("world"));
        Either<Either<String, Double>, Double> either5 = new Left<>(new Right<>(2.0));
        Either<String, Double> either6 = Either.joinLeft(either3);
        Either<String, Double> either7 = Either.joinLeft(either4);
        Either<String, Double> either8 = Either.joinLeft(either5);
        Assert.assertTrue(either6.isRight() && either6.right().get() == 1.0);
        Assert.assertTrue(either7.isLeft() && "world".equals(either7.left().get()));
        Assert.assertTrue(either8.isRight() && either8.right().get() == 2.0);
    }

    @Test
    public void testJoinRight() {
        Either<String, Either<String, Double>> either3 = new Left<>("hello");
        Either<String, Either<String, Double>> either4 = new Right<>(new Left<>("world"));
        Either<String, Either<String, Double>> either5 = new Right<>(new Right<>(3.0));
        Either<String, Double> either6 = Either.joinRight(either3);
        Either<String, Double> either7 = Either.joinRight(either4);
        Either<String, Double> either8 = Either.joinRight(either5);
        Assert.assertTrue(either6.isLeft() && "hello".equals(either6.left().get()));
        Assert.assertTrue(either7.isLeft() && "world".equals(either7.left().get()));
        Assert.assertTrue(either8.isRight() && either8.right().get() == 3.0);
    }

    @Test
    public void testSwap() {
        Either<Double, String> either3 = either1.swap();
        Either<Double, String> either4 = either2.swap();
        Assert.assertTrue(either3.isLeft() && either3.left().get() == 3.14);
        Assert.assertTrue(either4.isRight() && "hello".equals(either4.right().get()));
    }

    @Test
    public void testToOption() {
        Optional<Double> optionalDouble1 = either1.toOptional();
        Optional<Double> optionalDouble2 = either2.toOptional();
        Assert.assertTrue(optionalDouble1.isPresent() && optionalDouble1.get() == 3.14);
        Assert.assertFalse(optionalDouble2.isPresent());
    }

    @Test
    public void testToList() {
        List<Double> doubleList1 = either1.toList();
        List<Double> doubleList2 = either2.toList();
        Assert.assertTrue(doubleList1.contains(3.14));
        Assert.assertFalse(doubleList2.contains(3.14));
    }

    @Test
    public void testToTry() {
        Try<Double> doubleTry1 = either1.toTry();
        Try<Double> doubleTry2 = either2.toTry();
        Assert.assertTrue(doubleTry1.isSuccess() && doubleTry1.get() == 3.14);
        Assert.assertTrue(doubleTry2.isFailure() && "This object isn't instance of Right".equals(doubleTry2.failed().get().getMessage()));
    }
}
