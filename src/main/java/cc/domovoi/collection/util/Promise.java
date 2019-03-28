package cc.domovoi.collection.util;

import java.util.concurrent.CompletableFuture;

/**
 * Promise is an object which can be completed with a value or failed
 * with an exception.
 * <p>
 * If the promise has already been fulfilled, failed or has timed out,
 * calling this method will throw an IllegalStateException.
 * <p>
 * If the throwable used to fail this promise is an error, a control exception
 * or an interrupted exception, it will be wrapped as a cause within an
 * `ExecutionException` which will fail the promise.
 * <p>
 * Note: Using this method may result in non-deterministic concurrent programs.
 *
 * @param <T> Element type of this Promise
 */
public class Promise<T> {

    private CompletableFuture<T> _future;

    private Boolean _completedFlag;

    public Promise() {
        this._future = new CompletableFuture<>();
        this._completedFlag = false;
    }

    /**
     * Completes the promise with either an exception or a value.
     *
     * @param result Either the value or the exception to complete the promise with.
     * @return A completed Promise instance if it is uncompleted, or throw `IllegalStateException`.
     */
    public Promise<T> complete(Try<T> result) {
        if (tryComplete(result)) {
            return this;
        } else {
            throw new IllegalStateException("Promise already completed.");
        }
    }

    /**
     * Completes this promise with the specified future, once that future is completed.
     *
     * @param other the specified future
     * @return This promise
     */
    public Promise<T> completeWith(CompletableFuture<T> other) {
        return tryCompleteWith(other);
    }

    /**
     * Completes the promise with an exception.
     *
     * @param cause The throwable to complete the promise with.
     * @return This promise
     */
    public Promise<T> failure(Throwable cause) {
        return complete(new Failure<>(cause));
    }

    /**
     * Future containing the value of this promise.
     *
     * @return Future containing the value of this promise.
     */
    public CompletableFuture<T> future() {
        return _future;
    }

    /**
     * Returns whether the promise has already been completed with
     * a value or an exception.
     *
     * @return `true` if the promise is already completed, `false` otherwise
     */
    public Boolean isCompleted() {
        return _completedFlag;
    }

    /**
     * Completes the promise with a value.
     *
     * @param value The value to complete the promise with.
     * @return This promise
     */
    public Promise<T> success(T value) {
        return complete(new Success<>(value));
    }

    /**
     * Tries to complete the promise with either a value or the exception.
     *
     * @param result A `Try` instance
     * @return If the promise has already been completed returns `false`, or `true` otherwise.
     */
    public Boolean tryComplete(Try<T> result) {
        if (_completedFlag) {
            return false;
        } else {
            if (result.isSuccess()) {
                _future.complete(result.get());
            } else {
                _future.completeExceptionally(result.failed().get());
            }
            _completedFlag = true;
            return true;
        }
    }

    /**
     * Attempts to complete this promise with the specified future, once that future is completed.
     *
     * @param other the specified future
     * @return This promise
     */
    public Promise<T> tryCompleteWith(CompletableFuture<T> other) {
        if (!_completedFlag) {
            other.whenComplete((result, cause) -> {
                if (cause == null) {
                    _future.complete(result);
                } else {
                    _future.completeExceptionally(cause);
                }
                _completedFlag = true;
            });
        }
        return this;
    }

    /**
     * Tries to complete the promise with an exception.
     *
     * @param cause The throwable to complete the promise with.
     * @return If the promise has already been completed returns `false`, or `true` otherwise.
     */
    public Boolean tryFailure(Throwable cause) {
        return tryComplete(new Failure<>(cause));
    }

    /**
     * Tries to complete the promise with a value.
     *
     * @param value a given value
     * @return If the promise has already been completed returns `false`, or `true` otherwise.
     */
    public Boolean trySuccess(T value) {
        return tryComplete(new Success<>(value));
    }

    /**
     * Creates a promise object which can be completed with a value.
     *
     * @param <T1> the type of the value in the promise
     * @return the newly created `Promise` object
     */
    public static <T1> Promise<T1> apply() {
        return new Promise<>();
    }

    /**
     * Creates an already completed Promise with the specified exception.
     *
     * @param throwable The throwable to complete the promise with.
     * @param <T1>      the type of the value in the promise
     * @return the newly created `Promise` object
     */
    public static <T1> Promise<T1> failed(Throwable throwable) {
        return new Promise<T1>().failure(throwable);
    }

    /**
     * Creates an already completed Promise with the specified result or exception.
     *
     * @param result A `Try` instance
     * @param <T1>   the type of the value in the promise
     * @return the newly created `Promise` object
     */
    public static <T1> Promise<T1> fromTry(Try<T1> result) {
        return new Promise<T1>().complete(result);
    }

    /**
     * Creates an already completed Promise with the specified result.
     *
     * @param result the specified result
     * @param <T1>   the type of the value in the promise
     * @return the newly created `Promise` object
     */
    public static <T1> Promise<T1> successful(T1 result) {
        return new Promise<T1>().success(result);
    }

}
