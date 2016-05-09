package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.lang.String.format;

public abstract class BusinessFlow<Sad, Happy, Bias> {

    final Sad sad;
    final Happy happy;
    final Exception technicalFailure;

    BusinessFlow(Sad sad, Happy happy, Exception technicalFailure) {
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

    public abstract Optional<Bias> toOptional();

    public Bias get() {
        return toOptional().orElseThrow(() -> new RuntimeException(format("Not present. Happy path was '%s'. Sad path was '%s'. Exception was '%s'.", happy, sad, technicalFailure)));
    }

    public Bias orElse(Bias alternative) {
        return toOptional().orElse(alternative);
    }

    public Bias orElseGet(Supplier<Bias> alternativeSupplier) {
        return toOptional().orElseGet(alternativeSupplier);
    }

    public <X extends Throwable> Bias orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return toOptional().orElseThrow(exceptionSupplier);
    }

    public TechnicalFailure<Sad, Happy> ifTechnicalFailure() {
        return new TechnicalFailure<>(sad, happy, technicalFailure);
    }

    public SadPath<Sad, Happy> ifSad() {
        return new SadPath<>(sad, happy, technicalFailure);
    }

    public HappyPath<Sad, Happy> ifHappy() {
        return new HappyPath<>(sad, happy, technicalFailure);
    }
}
