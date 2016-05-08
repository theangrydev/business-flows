package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;

public class SadPath<Sad, Happy> extends BusinessFlowProjection<Sad, Happy> {

    SadPath(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public BusinessFlow<Sad, Happy> peek(Peek<Sad> peek) {
        return then(sad -> {
            peek.peek(sad);
            return BusinessFlow.sadPath(sad);
        });
    }

    public <NewSad> BusinessFlow<NewSad, Happy> then(Mapping<Sad, BusinessFlow<NewSad, Happy>> action) {
        return join(sad -> {
            try {
                return action.map(sad);
            } catch (Exception technicalFailure) {
                return BusinessFlow.technicalFailure(technicalFailure);
            }
        }, BusinessFlow::happyPath, BusinessFlow::technicalFailure);
    }

    public <NewSad> BusinessFlow<NewSad, Happy> map(Mapping<Sad, NewSad> mapping) {
        return then(mapping.andThen(BusinessFlow::sadPath));
    }

    public HappyFlow<Happy> technicalFailure(Function<Sad, Exception> sadToTechnicalFailure) {
        return join(sadToTechnicalFailure.andThen(HappyFlow::technicalFailure), HappyFlow::happyPath, HappyFlow::technicalFailure);
    }

    public Sad get() {
        return sadPath().orElseThrow(() -> new RuntimeException(format("Sad path not present. Happy path was '%s'. Exception was '%s'", happy, technicalFailure)));
    }

    private Optional<Sad> sadPath() {
        return Optional.ofNullable(sad);
    }
}
