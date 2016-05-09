package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

public class HappyPath<Sad, Happy> extends Projection<Sad, Happy, Happy> {

    HappyPath(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    @Override
    public Optional<Happy> toOptional() {
        return Optional.ofNullable(happy);
    }

    public static <Sad, Happy> HappyPath<Sad, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        return happyAttempt(happyAttempt.andThen(HappyPath::happyPath), HappyPath::technicalFailure);
    }

    public static <Result> Result happyAttempt(HappyAttempt<Result> happyAttempt, Function<Exception, Result> failureHandler) {
        try {
            return happyAttempt.happy();
        } catch (Exception technicalFailure) {
            return failureHandler.apply(technicalFailure);
        }
    }

    public static <Sad, Happy> HappyPath<Sad, Happy> happyPath(Happy happy) {
        return new HappyPath<>(null, happy, null);
    }

    public static <Sad, Happy> HappyPath<Sad, Happy> sadPath(Sad sad) {
        return new HappyPath<>(sad, null, null);
    }

    public static <Sad, Happy> HappyPath<Sad, Happy> technicalFailure(Exception technicalFailure) {
        return new HappyPath<>(null, null, technicalFailure);
    }

    public <NewHappy, Result extends HappyPath<Sad, NewHappy>> HappyPath<Sad, NewHappy> then(Mapping<Happy, Result> action) {
        return join(HappyPath::sadPath, happy -> {
            try {
                return action.map(happy);
            } catch (Exception technicalFailure) {
                return HappyPath.technicalFailure(technicalFailure);
            }
        }, HappyPath::technicalFailure);
    }

    public <NewHappy> HappyPath<Sad, NewHappy> map(Mapping<Happy, NewHappy> mapping) {
        return then(mapping.andThen(HappyPath::happyPath));
    }

    public HappyPath<Sad, Happy> attempt(ActionThatMightFail<Sad, Happy> actionThatMightFail) {
        return then(happy -> actionThatMightFail.attempt(happy).map(HappyPath::<Sad, Happy>sadPath).orElse(HappyPath.happyPath(happy)));
    }

    public HappyPath<Sad, Happy> peek(Peek<Happy> peek) {
        return then(happy -> {
            peek.peek(happy);
            return this;
        });
    }
}
