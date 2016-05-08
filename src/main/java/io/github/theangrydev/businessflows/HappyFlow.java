package io.github.theangrydev.businessflows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;

public class HappyFlow<Happy> {

    final Happy happyPath;
    final Exception exceptionPath;

    HappyFlow(Happy happyPath, Exception exceptionPath) {
        this.happyPath = happyPath;
        this.exceptionPath = exceptionPath;
    }

    public static <Happy> HappyFlow<Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        try {
            return happyPath(happyAttempt.happy());
        } catch (Exception exception) {
            return technicalFailure(exception);
        }
    }

    public static <Happy> HappyFlow<Happy> happyPath(Happy happyPath) {
        return new HappyFlow<>(happyPath, null);
    }

    public static <Happy> HappyFlow<Happy> technicalFailure(Exception exception) {
        return new HappyFlow<>(null, exception);
    }

    @SafeVarargs
    public final <Sad> ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return join(happy -> validate(happy, validators), ValidationFlow::technicalFailure);
    }

    public <Result> Result join(Function<Happy, Result> happyJoiner, Function<Exception, Result> exceptionJoiner) {
        return Optional.ofNullable(happyPath).map(happyJoiner)
                    .orElseGet(() -> Optional.ofNullable(exceptionPath).map(exceptionJoiner)
                            .orElseThrow(() -> new RuntimeException("Impossible scenario. There must always be a happy or exception.")));
    }

    public Optional<Exception> technicalFailure() {
        return Optional.ofNullable(exceptionPath);
    }

    private <Sad> ValidationFlow<Sad, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>[] validators) {
        List<Sad> failures = new ArrayList<>();
        for (ActionThatMightFail<Sad, Happy> validator : validators) {
            BusinessFlow<Sad, Happy> attempt =  attempt(validator);
            if (attempt.exceptionPath != null) {
                return ValidationFlow.technicalFailure(attempt.exceptionPath);
            }
            attempt.sadPath().peek(failures::add);
        }
        if (failures.isEmpty()) {
            return ValidationFlow.happyPath(happy);
        } else {
            return ValidationFlow.sadPath(failures);
        }
    }

    public  <Sad, NewHappy> BusinessFlow<Sad, NewHappy> then(HappyMapping<Happy, BusinessFlow<Sad, NewHappy>> action) {
        return new BusinessFlow<Sad, Happy>(null, happyPath, exceptionPath).then(action);
    }

    public <NewHappy> HappyFlow<NewHappy> map(HappyMapping<Happy, NewHappy> mapping) {
        return flatMap(mapping.andThen(happy -> new HappyFlow<>(happy, exceptionPath)));
    }

    private <NewHappy> HappyFlow<NewHappy> flatMap(HappyMapping<Happy, HappyFlow<NewHappy>> action) {
        return happyPath().map(happy -> tryHappyAction(happy, action)).orElse(HappyFlow.technicalFailure(exceptionPath));
    }

    public <Sad> BusinessFlow<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail) {
        return new BusinessFlow<Sad, Happy>(null, happyPath, exceptionPath).attempt(actionThatMightFail);
    }

    public HappyFlow<Happy> peek(Peek<Happy> peek) {
        return flatMap(happy -> {
            peek.peek(happy);
            return this;
        });
    }

    public Happy get() {
        return happyPath().orElseThrow(() -> new RuntimeException(format("Happy path not present. Exception was '%s'", exceptionPath)));
    }

    private <NewHappy> HappyFlow<NewHappy> tryHappyAction(Happy happy, HappyMapping<Happy, HappyFlow<NewHappy>> action) {
        return tryCatch(HappyFlow::technicalFailure, () -> action.map(happy));
    }

    private Optional<Happy> happyPath() {
        return Optional.ofNullable(happyPath);
    }

    private <Result> Result tryCatch(Function<Exception, Result> onException, BusinessFlowProjection.SupplierThatMightThrowException<Result> something) {
        try {
            return something.supply();
        } catch (Exception exception) {
            return onException.apply(exception);
        }
    }
}
