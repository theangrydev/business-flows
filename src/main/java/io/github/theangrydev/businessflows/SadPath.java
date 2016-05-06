package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;

public class SadPath<Sad, Happy>{

    private final Either<Sad, Happy> either;
    private final TechnicalFailure<Sad> technicalFailure;

    public SadPath(Either<Sad, Happy> either, TechnicalFailure<Sad> technicalFailure) {
        this.either = either;
        this.technicalFailure = technicalFailure;
    }

    public Sad get() {
        return either.left().orElseThrow(() -> new RuntimeException("Sad path not present"));
    }
}
