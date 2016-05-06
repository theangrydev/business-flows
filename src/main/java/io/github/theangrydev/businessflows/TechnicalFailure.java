package io.github.theangrydev.businessflows;

import java.util.function.Function;

@FunctionalInterface
public interface TechnicalFailure<Sad> {
    Sad wrap(Exception exception);
    default <NewSad> TechnicalFailure<NewSad> andThen(Function<Sad, NewSad> after) {
        return happy -> after.apply(wrap(happy));
    }
}
