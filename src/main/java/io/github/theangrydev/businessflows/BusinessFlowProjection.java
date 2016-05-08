package io.github.theangrydev.businessflows;

import java.util.function.Function;

abstract class BusinessFlowProjection<Sad, Happy> {

    final Sad sad;
    final Happy happy;
    final Exception technicalFailure;

    BusinessFlowProjection(Sad sad, Happy happy, Exception technicalFailure) {
        this.sad = sad;
        this.happy = happy;
        this.technicalFailure = technicalFailure;
    }

    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner) {
        if (happy != null) {
            return happyJoiner.apply(happy);
        } else if (sad != null) {
            return sadJoiner.apply(sad);
        } else if (technicalFailure != null) {
            return technicalFailureJoiner.apply(technicalFailure);
        } else {
            throw new IllegalStateException("Impossible scenario. There must always be a happy or sad or technical failure.");
        }
    }

    public TechnicalFailure<Sad, Happy> ifFailure() {
        return new TechnicalFailure<>(sad, happy, technicalFailure);
    }

    @FunctionalInterface
    interface SupplierThatMightThrowException<Result> {
        Result supply() throws Exception;
    }

}
