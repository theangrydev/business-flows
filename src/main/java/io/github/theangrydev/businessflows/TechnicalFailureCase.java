package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

public class TechnicalFailureCase<Sad, Happy> implements BusinessCase<Sad, Happy> {

    private final Exception technicalFailure;

    public TechnicalFailureCase(Exception technicalFailure) {
        this.technicalFailure = technicalFailure;
    }

    @Override
    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner) {
        return technicalFailureJoiner.apply(technicalFailure);
    }

    @Override
    public Optional<Happy> happyOptional() {
        return Optional.empty();
    }

    @Override
    public Optional<Sad> sadOptional() {
        return Optional.empty();
    }

    @Override
    public Optional<Exception> technicalFailureOptional() {
        return Optional.of(technicalFailure);
    }

    @Override
    public String toString() {
        return "Technical Failure: " + technicalFailure;
    }
}
