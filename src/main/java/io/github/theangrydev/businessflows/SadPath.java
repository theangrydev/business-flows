package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Function;

import static java.lang.String.format;

public class SadPath<Sad, Happy> extends BusinessFlowProjection<Sad, Happy> {

    SadPath(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public BusinessFlow<Sad, Happy> peek(Peek<Sad> peek) {
        return join(sad -> {
            try {
                peek.peek(sad);
                return BusinessFlow.sadPath(sad);
            } catch (Exception e) {
                return BusinessFlow.technicalFailure(e);
            }
        }, BusinessFlow::happyPath, BusinessFlow::technicalFailure);
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
