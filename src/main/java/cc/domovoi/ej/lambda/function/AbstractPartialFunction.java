package cc.domovoi.ej.lambda.function;

import java.util.function.Function;

public abstract class AbstractPartialFunction<T, R> implements Function<T, R>, PartialFunction<T, R> {
    @Override
    public R apply(T t) {
        return applyOrElse(t, PartialFunctionCompanion.empty());
    }
}
