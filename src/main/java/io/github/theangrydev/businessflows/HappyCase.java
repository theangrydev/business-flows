package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

public class HappyCase<Sad, Happy> implements BusinessCase<Sad, Happy> {

    private final Happy happy;

    public HappyCase(Happy happy) {
        this.happy = happy;
    }

    @Override
    public <Result> Result join(Function<Sad, Result> sadJoiner, Function<Happy, Result> happyJoiner, Function<Exception, Result> technicalFailureJoiner) {
        return happyJoiner.apply(happy);
    }

    @Override
    public Optional<Happy> happy() {
        return Optional.of(happy);
    }

    @Override
    public Optional<Sad> sad() {
        return Optional.empty();
    }

    @Override
    public Optional<Exception> technicalFailure() {
        return Optional.empty();
    }
}
