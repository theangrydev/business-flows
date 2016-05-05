package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;
import com.codepoetics.ambivalence.RightProjection;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static com.codepoetics.ambivalence.Either.ofRight;
import static com.codepoetics.ambivalence.Eithers.split;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;

public class BusinessFlow<Sad, Happy> {

    private final Either<Sad, Happy> either;
    private final Function<Exception, Sad> technicalFailure;

    private BusinessFlow(Either<Sad, Happy> either, Function<Exception, Sad> technicalFailure) {
        this.either = either;
        this.technicalFailure = technicalFailure;
    }

    public interface HappyAttempt<Sad extends Exception, Happy> {
        Happy happy() throws Sad;
    }

    //TODO: think about this
    @SuppressWarnings("unchecked")
    public static <Sad extends Exception, Happy> BusinessFlow<Sad, Happy> businessFlow(HappyAttempt<Sad, Happy> happyAttempt) {
        try {
            return happyPath(happyAttempt.happy(), e -> {throw new RuntimeException();});
        } catch (Exception sad) {
            return sadPath((Sad) sad, e -> {throw new RuntimeException();});
        }
    }

    public static <Sad, Happy > BusinessFlow<Sad, Happy> happyPath(Happy happy, Function<Exception, Sad> technicalFailure) {
        return new BusinessFlow<>(ofRight(happy), technicalFailure);
    }

    public static <Sad, Happy > BusinessFlow<Sad, Happy> sadPath(Sad sad, Function<Exception, Sad> technicalFailure) {
        return new BusinessFlow<>(Either.ofLeft(sad), technicalFailure);
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> then(Function<Happy, BusinessFlow<Sad, NewHappy>> action) {
        return businessFlow(either.right().flatMap(happy -> tryHappyAction(happy, action).either));
    }

    public <NewHappy> BusinessFlow<Sad, NewHappy> map(Function<Happy, NewHappy> mapping) {
        return businessFlow(either.right().flatMap(happy -> tryHappyMapping(happy, mapping).either));
    }

    public BusinessFlow<Sad, Happy> attempt(Function<Happy, Optional<Sad>> actionThatMightFail) {
        return businessFlow(either.right().flatMap(happy -> attempt(actionThatMightFail, happy)));
    }

    @SafeVarargs
    public final BusinessFlow<Set<Sad>, Happy> attempt(Function<Happy, Optional<Sad>>... validators) {
        RightProjection<Set<Sad>, Happy> attempt = either.left().map(Collections::singleton).right().flatMap(happy -> attempt(happy, validators));
        return new BusinessFlow<>(attempt, technicalFailure.andThen(Collections::singleton));
    }

    private Either<Set<Sad>, Happy> attempt(Happy happy, Function<Happy, Optional<Sad>>[] validators) {
        List<Sad> failures = stream(validators).map(validator -> attempt(validator, happy)).collect(split()).getLefts();
        if (failures.isEmpty()) {
            return Either.ofRight(happy);
        } else {
            return Either.ofLeft(failures.stream().collect(toSet()));
        }
    }

    private Either<Sad, Happy> attempt(Function<Happy, Optional<Sad>> actionThatMightFail, Happy happy) {
        return actionThatMightFail.apply(happy).map(Either::<Sad, Happy>ofLeft).orElse(ofRight(happy));
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> businessFlow(Either<Sad, NewHappy> either) {
        return new BusinessFlow<>(either, technicalFailure);
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> happyPath(NewHappy happy) {
        return happyPath(happy, technicalFailure);
    }

    private BusinessFlow<Sad, Happy> sadPath(Sad sad) {
        return sadPath(sad, technicalFailure);
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> tryHappyMapping(Happy happy, Function<Happy, NewHappy> mapping) {
        return tryHappyAction(happy, mapping.andThen(this::happyPath));
    }

    private <NewHappy> BusinessFlow<Sad, NewHappy> tryHappyAction(Happy happy, Function<Happy, BusinessFlow<Sad, NewHappy>> action) {
        try {
            return action.apply(happy);
        } catch (RuntimeException runtimeException) {
            return sadPath(technicalFailure.apply(runtimeException), technicalFailure);
        }
    }

    public SadPath<Sad, Happy> sadPath() {
        return new SadPath<>(either, technicalFailure);
    }

    public Happy get() {
        return either.right().orElseThrow(() -> new RuntimeException("Happy path not present"));
    }
}
