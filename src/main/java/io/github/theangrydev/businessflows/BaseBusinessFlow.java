package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;

import java.util.Optional;
import java.util.function.Function;

import static com.codepoetics.ambivalence.Either.ofRight;
import static java.lang.String.format;

abstract class BaseBusinessFlow<Sad, Happy> extends Projection<Sad, Happy> {

    BaseBusinessFlow(Either<Sad, Happy> either, UncaughtExceptionHandler uncaughtExceptionHandler, TechnicalFailure<Sad> technicalFailure) {
        super(either, uncaughtExceptionHandler, technicalFailure);
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> then(HappyMapping<Happy, BaseBusinessFlow<Sad, NewHappy>> action) {
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

    public SadPath<Sad, Happy> sadPath() {
        return new SadPath<>(either, uncaughtExceptionHandler, technicalFailure);
    }

    public Happy get() {
        return either.right().orElseThrow(() -> new RuntimeException(format("Happy path not present. Sad path was '%s'", either.left().toOptional())));
    }

    Either<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail, Happy happy) {
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

    private <NewHappy> Either<Sad, NewHappy> tryHappyAction(Happy happy, HappyMapping<Happy, BaseBusinessFlow<Sad, NewHappy>> action) {
        return tryCatch(Either::ofLeft, () -> action.map(happy).either);
    }
}
