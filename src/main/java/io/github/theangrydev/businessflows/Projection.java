package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

abstract class Projection<Sad, Happy> {

    final Sad sadPath;
    final Happy happyPath;
    final Exception exceptionPath;

    Projection(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        this.sadPath = sadPath;
        this.happyPath = happyPath;
        this.exceptionPath = exceptionPath;
    }

    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> exceptionJoiner) {
        return Optional.ofNullable(happyPath).map(happyJoiner)
                .orElseGet(() -> Optional.ofNullable(sadPath).map(sadJoiner)
                        .orElseGet(() -> Optional.ofNullable(exceptionPath).map(exceptionJoiner)
                                .orElseThrow(() -> new RuntimeException("Impossible scenario. There must always be a happy or sad or exception."))));
    }

    @FunctionalInterface
    interface SupplierThatMightThrowException<Result> {
        Result supply() throws Exception;
    }

    <Result> Result tryCatch(Function<Exception, Result> onException, SupplierThatMightThrowException<Result> something) {
        try {
            return something.supply();
        } catch (Exception exception) {
            return onException.apply(exception);
        }
    }
}
