package io.github.theangrydev.businessflows;

@FunctionalInterface
public interface Mapping<Happy, NewHappy> {
    NewHappy map(Happy happy) throws Exception;
    default <NewNewHappy> Mapping<Happy, NewNewHappy> andThen(Mapping<NewHappy, NewNewHappy> after) {
        return happy -> after.map(map(happy));
    }
}
