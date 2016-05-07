package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;
import com.codepoetics.ambivalence.RightProjection;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static com.codepoetics.ambivalence.Either.ofRight;
import static com.codepoetics.ambivalence.Eithers.split;
import static java.util.Arrays.stream;

public class BusinessFlow<Sad, Happy> {

    private final Either<Sad, Happy> either;
    private final UncaughtExceptionHandler uncaughtExceptionHandler;
    private final TechnicalFailure<Sad> technicalFailure;

    BusinessFlow(Either<Sad, Happy> either, UncaughtExceptionHandler uncaughtExceptionHandler, TechnicalFailure<Sad> technicalFailure) {
        this.either = either;
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
        this.technicalFailure = technicalFailure;
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> then(HappyMapping<Happy, BusinessFlow<Sad, NewHappy>> action) {
        return happyPath(either.right().flatMap(happy -> tryHappyAction(happy, action)));
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> map(HappyMapping<Happy, NewHappy> mapping) {
        return happyPath(either.right().flatMap(happy -> tryHappyMapping(happy, mapping)));
    }

    public BusinessFlow<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail) {
        return happyPath(either.right().flatMap(happy -> attempt(actionThatMightFail, happy)));
    }

    public BusinessFlow<Sad, Happy> peek(Peek<Happy> peek) {
        return happyPath(either.right().flatMap(happy -> tryToConsume(happy, peek)));
    }

    public <Result> Result join(Function<Happy, Result> happyJoiner, Function<Sad, Result> sadJoiner) {
        return either.join(sadJoiner, happyJoiner);
    }

    @SafeVarargs
    public final BusinessFlow<List<Sad>, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        RightProjection<List<Sad>, Happy> attempt = either.left().map(Collections::singletonList).right().flatMap(happy -> validate(happy, validators));
        return new BusinessFlow<>(attempt, uncaughtExceptionHandler, technicalFailure.andThen(Collections::singletonList));
    }

    public SadPath<Sad, Happy> sadPath() {
        return new SadPath<>(either, technicalFailure);
    }

    public Happy get() {
        return either.right().orElseThrow(() -> new RuntimeException("Happy path not present"));
    }

    private <Argument> Either<Sad, Happy> tryToConsume(Argument argument, Peek<Argument> consumer) {
        return tryCatch(Either::ofLeft, () -> {consumer.peek(argument); return either; });
    }

    private Either<List<Sad>, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>[] validators) {
        List<Sad> failures = stream(validators).map(validator -> attempt(validator, happy)).collect(split()).getLefts();
        if (failures.isEmpty()) {
            return Either.ofRight(happy);
        } else {
            return Either.ofLeft(failures);
        }
    }

    private Either<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail, Happy happy) {
        return tryActionThatMightFail(actionThatMightFail, happy).map(Either::<Sad, Happy>ofLeft).orElse(ofRight(happy));
    }

    private Optional<Sad> tryActionThatMightFail(ActionThatMightFail<Sad, Happy> actionThatMightFail, Happy happy) {
        return tryCatch(Optional::of, () -> actionThatMightFail.attempt(happy));
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> happyPath(Either<Sad, NewHappy> either) {
        return new BusinessFlow<>(either, uncaughtExceptionHandler, technicalFailure);
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> happyPath(NewHappy happy) {
        return happyPath(Either.ofRight(happy));
    }

    private <NewHappy> Either<Sad, NewHappy> tryHappyMapping(Happy happy, HappyMapping<Happy, NewHappy> mapping) {
        return tryHappyAction(happy, mapping.andThen(this::happyPath));
    }

    private <NewHappy> Either<Sad, NewHappy> tryHappyAction(Happy happy, HappyMapping<Happy, BusinessFlow<Sad, NewHappy>> action) {
        return tryCatch(Either::ofLeft, () -> action.map(happy).either);
    }

    @FunctionalInterface
    private interface SupplierThatMightThrowException<Result> {
        Result supply() throws Exception;
    }

    private <Result> Result tryCatch(Function<Sad, Result> onException, SupplierThatMightThrowException<Result> something) {
        try {
            return something.supply();
        } catch (Exception exception) {
            uncaughtExceptionHandler.handle(exception);
            return onException.apply(technicalFailure.wrap(exception));
        }
    }
}
