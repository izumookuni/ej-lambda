package cc.domovoi.collection.test;

import cc.domovoi.collection.util.Promise;
import cc.domovoi.collection.util.Try;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;

public class PromiseTest {

    @Test
    public void testConstructor1() {
        Promise<Integer> integerPromise = Promise.apply();
        integerPromise.success(3);
        Assert.assertTrue(integerPromise.isCompleted());
        integerPromise.future().thenAccept(integer -> Assert.assertTrue(integer == 3));
    }

    @Test
    public void testConstructor2() {
        Promise<Integer> integerPromise = Promise.apply();
        integerPromise.failure(new RuntimeException("error"));
        Assert.assertTrue(integerPromise.future().isCompletedExceptionally());
        integerPromise.future().whenComplete((result, cause) -> {
            Assert.assertTrue("error".equals(cause.getMessage()));
        });
    }

    @Test
    public void testConstructor3() {
        Promise<Integer> integerPromise = Promise.successful(3);
        Assert.assertTrue(integerPromise.isCompleted());
        integerPromise.future().thenAccept(integer -> Assert.assertTrue(integer == 3));
    }

    @Test
    public void testConstructor4() {
        Promise<Integer> integerPromise = Promise.failed(new RuntimeException("error"));
        Assert.assertTrue(integerPromise.future().isCompletedExceptionally());
        integerPromise.future().whenComplete((result, cause) -> {
            Assert.assertTrue("error".equals(cause.getMessage()));
        });
    }

    @Test
    public void testConstructor5() {
        Promise<Integer> integerPromise1 = Promise.fromTry(Try.apply(() -> 3 / 2));
        Promise<Integer> integerPromise2 = Promise.fromTry(Try.apply(() -> 3 / 0));
        Assert.assertTrue(integerPromise1.future().isDone());
        Assert.assertTrue(integerPromise2.future().isCompletedExceptionally());
        integerPromise1.future().thenAccept(integer -> Assert.assertTrue(integer == 1));
        integerPromise2.future().whenComplete((result, cause) -> {
            Assert.assertTrue("/ by zero".equals(cause.getMessage()));
        });
    }

    @Test
    public void testTry() {
        Promise<Integer> integerPromise = Promise.apply();
        Boolean b1 = integerPromise.trySuccess(3);
        Boolean b2 = integerPromise.trySuccess(4);
        Boolean b3 = integerPromise.tryFailure(new RuntimeException("error"));
        Assert.assertTrue(b1);
        Assert.assertFalse(b2);
        Assert.assertFalse(b3);
        Assert.assertTrue(integerPromise.isCompleted());
        integerPromise.future().thenAccept(integer -> Assert.assertTrue(integer == 3));
    }

    @Test
    public void testTryComplete() {
        Promise<Integer> integerPromise = Promise.apply();
        Boolean b1 = integerPromise.tryComplete(Try.apply(() -> 3));
        Boolean b2 = integerPromise.tryComplete(Try.apply(() -> 4));
        Assert.assertTrue(b1);
        Assert.assertFalse(b2);
        integerPromise.future().thenAccept(integer -> Assert.assertTrue(integer == 3));
    }

    @Test
    public void testTryCompleteWith() {
        Promise<Integer> integerPromise = Promise.apply();
        CompletableFuture<Integer> future = new CompletableFuture<>();
        integerPromise.tryCompleteWith(future);
        Assert.assertFalse(integerPromise.isCompleted());
        future.complete(3);
        Assert.assertTrue(integerPromise.isCompleted());
        Boolean b1 = integerPromise.trySuccess(4);
        Assert.assertFalse(b1);
        integerPromise.future().thenAccept(integer -> Assert.assertTrue(integer == 3));
    }
}
