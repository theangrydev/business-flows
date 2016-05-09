package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

public class SadPath<Sad, Happy> extends Projection<Sad, Happy, Sad> {

    SadPath(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public static <Sad, Happy> SadPath<Sad, Happy> sadPath(Sad sad) {
        return new SadPath<>(sad, null, null);
    }

    public static <Sad, Happy> SadPath<Sad, Happy> happyPath(Happy happy) {
        return new SadPath<>(null, happy, null);
    }

    public static <Sad, Happy> SadPath<Sad, Happy> technicalFailure(Exception technicalFailure) {
        return new SadPath<>(null, null, technicalFailure);
    }

    @Override
    public Optional<Sad> toOptional() {
        return Optional.ofNullable(sad);
    }

    public SadPath<Sad, Happy> peek(Peek<Sad> peek) {
        return then(sad -> {
            peek.peek(sad);
            return sadPath(sad);
        });
    }

    public <NewSad> SadPath<NewSad, Happy> then(Mapping<Sad, SadPath<NewSad, Happy>> action) {
        return join(sad -> {
            try {
                return action.map(sad);
            } catch (Exception technicalFailure) {
                return technicalFailure(technicalFailure);
            }
        }, SadPath::happyPath, SadPath::technicalFailure);
    }

    public HappyPath<Sad, Happy> recover(Mapping<Sad, Happy> recovery) {
        return this.<Sad>then(sad -> happyPath(recovery.map(sad))).ifHappy();
    }

    public <NewSad> SadPath<NewSad, Happy> map(Mapping<Sad, NewSad> mapping) {
        return then(mapping.andThen(SadPath::sadPath));
    }

    public TechnicalFailure<Sad, Happy> technicalFailure(Function<Sad, Exception> sadToTechnicalFailure) {
        return join(sadToTechnicalFailure.andThen(TechnicalFailure::technicalFailure), TechnicalFailure::happyPath, TechnicalFailure::technicalFailure);
    }
}
