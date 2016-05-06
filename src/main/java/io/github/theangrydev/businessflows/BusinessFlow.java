package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;
import com.codepoetics.ambivalence.RightProjection;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.codepoetics.ambivalence.Either.ofRight;
import static com.codepoetics.ambivalence.Eithers.split;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public class BusinessFlow<Sad, Happy> {

    private final Either<Sad, Happy> either;
    private final TechnicalFailure<Sad> technicalFailure;

    private BusinessFlow(Either<Sad, Happy> either, TechnicalFailure<Sad> technicalFailure) {
        this.either = either;
        this.technicalFailure = technicalFailure;
    }

    public static <Happy> BusinessFlow<Exception, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        return happyAttempt(happyAttempt, exception -> exception);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt, TechnicalFailure<Sad> technicalFailure) {
        try {
            return happyPath(happyAttempt.happy(), technicalFailure);
        } catch (Exception sad) {
            return sadPath(technicalFailure.wrap(sad), technicalFailure);
        }
    }

    public static <Sad, Happy > BusinessFlow<Sad, Happy> happyPath(Happy happy, TechnicalFailure<Sad> technicalFailure) {
        return new BusinessFlow<>(ofRight(happy), technicalFailure);
    }

    public static <Sad, Happy > BusinessFlow<Sad, Happy> sadPath(Sad sad, TechnicalFailure<Sad> technicalFailure) {
        return new BusinessFlow<>(Either.ofLeft(sad), technicalFailure);
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

    public BusinessFlow<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy>... actionsThatMightFail) {
        BusinessFlow<Sad, Happy> attempt = this;
        for (ActionThatMightFail<Sad, Happy> actionThatMightFail : actionsThatMightFail) {
            attempt = attempt(actionThatMightFail);
            if (attempt.either.isLeft()) {
                return attempt;
            }
        }
        return attempt;
    }

    public BusinessFlow<Sad, Happy> peek(Consumer<Happy> consumer) {
        return happyPath(either.right().flatMap(happy -> tryToConsume(happy, consumer)));
    }

    public BusinessFlow<Sad, Happy> peek(Consumer<Happy> happyConsumer, Consumer<Sad> sadConsumer) {
        return happyPath(either.right().flatMap(happy -> tryToConsume(happy, happyConsumer)).left().flatMap(sad -> tryToConsume(sad, sadConsumer)));
    }

    public <Result> Result join(Function<Happy, Result> happyJoiner, Function<Sad, Result> sadJoiner) {
        return either.join(sadJoiner, happyJoiner);
    }

    private <Argument> Either<Sad, Happy> tryToConsume(Argument happy, Consumer<Argument> consumer) {
        try {
            consumer.accept(happy);
            return either;
        } catch (RuntimeException exception) {
            return Either.ofLeft(technicalFailure.wrap(exception));
        }
    }

    @SafeVarargs
    public final BusinessFlow<Set<Sad>, Happy> attemptAll(ActionThatMightFail<Sad, Happy>... validators) {
        RightProjection<Set<Sad>, Happy> attempt = either.left().map(Collections::singleton).right().flatMap(happy -> attemptAll(happy, validators));
        return new BusinessFlow<>(attempt, technicalFailure.andThen(Collections::singleton));
    }

    private Either<Set<Sad>, Happy> attemptAll(Happy happy, ActionThatMightFail<Sad, Happy>[] validators) {
        List<Sad> failures = stream(validators).map(validator -> attempt(validator, happy)).collect(split()).getLefts();
        if (failures.isEmpty()) {
            return Either.ofRight(happy);
        } else {
            return Either.ofLeft(failures.stream().collect(toSet()));
        }
    }

    private Either<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail, Happy happy) {
        return tryActionThatMightFail(actionThatMightFail, happy).map(Either::<Sad, Happy>ofLeft).orElse(ofRight(happy));
    }

    private Optional<Sad> tryActionThatMightFail(ActionThatMightFail<Sad, Happy> actionThatMightFail, Happy happy) {
        try {
            return actionThatMightFail.attempt(happy);
        } catch (Exception exception) {
            return Optional.of(technicalFailure.wrap(exception));
        }
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> happyPath(Either<Sad, NewHappy> either) {
        return new BusinessFlow<>(either, technicalFailure);
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> happyPath(NewHappy happy) {
        return happyPath(happy, technicalFailure);
    }

    private <NewHappy> Either<Sad, NewHappy> tryHappyMapping(Happy happy, HappyMapping<Happy, NewHappy> mapping) {
        return tryHappyAction(happy, mapping.andThen(this::happyPath));
    }

    private <NewHappy> Either<Sad, NewHappy> tryHappyAction(Happy happy, HappyMapping<Happy, BusinessFlow<Sad, NewHappy>> action) {
        try {
            return action.map(happy).either;
        } catch (Exception exception) {
            return Either.ofLeft(technicalFailure.wrap(exception));
        }
    }

    public SadPath<Sad, Happy> sadPath() {
        return new SadPath<>(either, technicalFailure);
    }

    public Happy get() {
        return either.right().orElseThrow(() -> new RuntimeException("Happy path not present"));
    }
}
