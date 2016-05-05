package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;

import java.util.function.Function;

public class SadPath<Sad, Happy>{

    private final Either<Sad, Happy> either;
    private final Function<Exception, Sad> technicalFailure;

    public SadPath(Either<Sad, Happy> either, Function<Exception, Sad> technicalFailure) {
        this.either = either;
        this.technicalFailure = technicalFailure;
    }

    public Sad get() {
        return either.left().orElseThrow(() -> new RuntimeException("Sad path not present"));
    }
}
