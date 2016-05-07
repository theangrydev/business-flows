package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;

import static java.lang.String.format;

public class SadPath<Sad, Happy> extends Projection<Sad, Happy> {

    protected SadPath(Either<Sad, Happy> either, UncaughtExceptionHandler uncaughtExceptionHandler, TechnicalFailure<Sad> technicalFailure) {
        super(either, uncaughtExceptionHandler, technicalFailure);
    }

    public BusinessFlow<Sad, Happy> peek(Peek<Sad> peek) {
        return new BusinessFlow<>(either.left().<Sad>flatMap(sad -> tryToConsume(sad, peek)), uncaughtExceptionHandler,  technicalFailure);
    }

    public Sad get() {
        return either.left().orElseThrow(() -> new RuntimeException(format("Sad path not present. Happy path was '%s'", either.right().toOptional())));
    }
}
