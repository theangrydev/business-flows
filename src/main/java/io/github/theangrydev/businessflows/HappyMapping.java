package io.github.theangrydev.businessflows;

@FunctionalInterface
public interface HappyMapping<Happy, NewHappy> {
    NewHappy map(Happy happy) throws Exception;
    default <NewNewHappy> HappyMapping<Happy, NewNewHappy> andThen(HappyMapping<NewHappy, NewNewHappy> after) {
        return happy -> after.map(map(happy));
    }
}
