package io.github.theangrydev.businessflows;

import java.util.Optional;

import static java.lang.String.format;

public class SadPath<Sad, Happy> extends Projection<Sad, Happy> {

    SadPath(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
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

    public Sad get() {
        return sadPath().orElseThrow(() -> new RuntimeException(format("Sad path not present. Happy path was '%s'. Exception was '%s'", happyPath, exceptionPath)));
    }

    private Optional<Sad> sadPath() {
        return Optional.ofNullable(sadPath);
    }
}
