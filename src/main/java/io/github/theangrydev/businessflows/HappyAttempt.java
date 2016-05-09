package io.github.theangrydev.businessflows;

@FunctionalInterface
public interface HappyAttempt<Happy> {
    Happy happy() throws Exception;
    default <NewHappy> HappyAttempt<NewHappy> andThen(Mapping<Happy, NewHappy> after) {
        return () -> after.map(happy());
    }
}
