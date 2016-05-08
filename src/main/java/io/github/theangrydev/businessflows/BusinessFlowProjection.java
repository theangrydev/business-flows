package io.github.theangrydev.businessflows;

import java.util.function.Function;

abstract class BusinessFlowProjection<Sad, Happy> {

    final Sad sadPath;
    final Happy happyPath;
    final Exception exceptionPath;

    BusinessFlowProjection(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        this.sadPath = sadPath;
        this.happyPath = happyPath;
        this.exceptionPath = exceptionPath;
    }

    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> exceptionJoiner) {
        if (happyPath != null) {
            return happyJoiner.apply(happyPath);
        } else if (sadPath != null) {
            return sadJoiner.apply(sadPath);
        } else if (exceptionPath != null) {
            return exceptionJoiner.apply(exceptionPath);
        } else {
            throw new IllegalStateException("Impossible scenario. There must always be a happy or sad or exception.");
        }
    }

    public HappyFlow<Happy> failIfSad(Function<Sad, Exception> failure) {
        return join(failure.andThen(HappyFlow::failure), HappyFlow::happyPath, HappyFlow::failure);
    }

    public Failure<Sad, Happy> ifFailure() {
        return new Failure<>(sadPath, happyPath, exceptionPath);
    }

    @FunctionalInterface
    interface SupplierThatMightThrowException<Result> {
        Result supply() throws Exception;
    }

}
