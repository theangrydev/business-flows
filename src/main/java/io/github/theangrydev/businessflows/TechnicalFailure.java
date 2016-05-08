package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.String.format;

public class TechnicalFailure<Sad, Happy> extends BusinessFlowProjection<Sad, Happy>  {
    TechnicalFailure(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    public BusinessFlow<Sad, Happy> peek(Consumer<Exception> peek) {
        return join(BusinessFlow::sadPath, BusinessFlow::happyPath, exception -> {
            try {
                peek.accept(exception);
                return BusinessFlow.technicalFailure(exception);
            } catch (Exception technicalFailure) {
                return BusinessFlow.technicalFailure(technicalFailure);
            }
        });
    }

    public Exception get() {
        return Optional.ofNullable(exceptionPath).orElseThrow(() -> new RuntimeException(format("Exception not present. Happy path was '%s'. Sad path was '%s'", happyPath, sadPath)));
    }
}
