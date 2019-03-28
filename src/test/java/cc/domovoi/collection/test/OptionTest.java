package cc.domovoi.collection.test;

import cc.domovoi.collection.util.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class OptionTest {

    private final Option<Integer> some = Some.apply(42);

    private final Option<Integer> none = None.unit();

    @Test
    public void testConstructor1() {
        Option<Integer> option1 = Some.apply(42);
        Assert.assertTrue(option1.nonEmpty() && option1.get() == 42);

        Option<Integer> option2 = None.unit();
        Assert.assertTrue(option2.isEmpty());
    }

    @Test
    public void testConstructor2() {
        Optional<Integer> optional1 = Optional.of(42);
        Option<Integer> option1 = Option.from(optional1);
        Assert.assertTrue(option1.nonEmpty() && option1.get() == 42);

        Optional<Integer> optional2 = Optional.empty();
        Option<Integer> option2 = Option.from(optional2);
        Assert.assertTrue(option2.isEmpty());
    }

    @Test
    public void testEquals() {
        Option<Integer> option1 = Some.apply(42);
        Option<Integer> option2 = None.unit();

        Assert.assertEquals(option1, some);
        Assert.assertNotEquals(option1, none);
        Assert.assertEquals(option2, none);
        Assert.assertNotEquals(option2, some);
    }

    @Test
    public void testContains() {
        Assert.assertTrue(some.contains(42));
        Assert.assertFalse(some.contains(1));
        Assert.assertFalse(none.contains(42));
    }

    @Test
    public void testExist() {
        Assert.assertTrue(some.exist(integer -> integer == 42));
        Assert.assertFalse(none.exist(integer -> integer == 42));
        Assert.assertFalse(none.exist(integer -> true));
    }

    @Test
    public void testFilter() {
        Assert.assertEquals(none.filter(integer -> true), none);
        Assert.assertEquals(some.filter(integer -> integer == 42), some);
        Assert.assertEquals(some.filter(integer -> integer < 0), none);

        Assert.assertEquals(none.filterNot(integer -> false), none);
        Assert.assertEquals(some.filterNot(integer -> integer == 42), none);
        Assert.assertEquals(some.filterNot(integer -> integer < 0), some);
    }

    @Test
    public void testFlatMap() {
        Assert.assertEquals(some.flatMap(integer -> Some.apply(1 + integer)), Some.apply(43));
        Assert.assertEquals(some.flatMap(integer -> None.<Integer>unit()), none);
        Assert.assertEquals(none.flatMap(integer -> Some.apply(1 + integer)), none);
    }

    @Test
    public void testFlatten() {
        Option<Option<Integer>> optionOption1 = Some.apply(some);
        Option<Option<Integer>> optionOption2 = Some.apply(none);
        Option<Option<Integer>> optionOption3 = none.map(integer -> some);
        Option<Option<Integer>> optionOption4 = none.map(integer -> none);

        Assert.assertEquals(Option.flatten(optionOption1), some);
        Assert.assertEquals(Option.flatten(optionOption2), none);
        Assert.assertEquals(Option.flatten(optionOption3), none);
        Assert.assertEquals(Option.flatten(optionOption4), none);
    }

    @Test
    public void testFold() {
        Assert.assertTrue(some.fold(1, integer -> integer + 1) == 43);
        Assert.assertTrue(none.fold(1, integer -> integer + 1) == 1);
    }

    @Test
    public void testForall() {
        Assert.assertTrue(some.forall(integer -> integer == 42));
        Assert.assertFalse(some.forall(integer -> integer == 1));
        Assert.assertTrue(none.forall(integer -> true));
        Assert.assertTrue(none.forall(integer -> false));
    }

    @Test
    public void testGetOrElse() {
        Assert.assertTrue(some.getOrElse(() -> 1) == 42);
        Assert.assertTrue(none.getOrElse(() -> 1) == 1);
    }

    @Test
    public void testForeach() {

        try {
            some.foreach(integer -> {
                throw new RuntimeException("this will be executed");
            });
        } catch (RuntimeException e) {
            Assert.assertTrue("this will be executed".equals(e.getMessage()));
        }

        none.foreach(integer -> {
            throw new RuntimeException("this will not be executed");
        });

    }

    @Test
    public void testMap() {
        Assert.assertEquals(some.map(integer -> integer + 1), Some.apply(43));
        Assert.assertEquals(none.map(integer -> integer + 1), none);
    }

    @Test
    public void testOrElse() {
        Assert.assertEquals(some.orElse(() -> Some.apply(1)), some);
        Assert.assertEquals(none.orElse(() -> Some.apply(1)), Some.apply(1));
    }

    @Test
    public void testOrNull() {
        Assert.assertTrue(some.orNull() == 42);
        Assert.assertTrue(none.orNull() == null);
    }

    @Test
    public void testToOptional() {
        Assert.assertEquals(some.toOptional(), Optional.of(42));
        Assert.assertEquals(none.toOptional(), Optional.<Integer>empty());
    }

    @Test
    public void testToEither() {
        Assert.assertEquals(some.toLeft(() -> "hello"), new Left<Integer, String>(42));
        Assert.assertEquals(some.toRight(() -> "hello"), new Right<String, Integer>(42));
        Assert.assertEquals(none.toLeft(() -> "hello"), new Right<Integer, String>("hello"));
        Assert.assertEquals(none.toRight(() -> "hello"), new Left<String, Integer>("hello"));
    }

    @Test
    public void testToList() {
        List<Integer> someList = some.toList();
        List<Integer> noneList = none.toList();
        Assert.assertTrue(someList.size() == 1 && someList.get(0) == 42);
        Assert.assertTrue(noneList.isEmpty());
    }



}
