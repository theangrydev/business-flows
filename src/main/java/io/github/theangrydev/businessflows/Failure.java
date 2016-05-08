package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.String.format;

public class Failure<Sad, Happy> extends BusinessFlowProjection<Sad, Happy>  {
    Failure(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    public BusinessFlow<Sad, Happy> peek(Consumer<Exception> peek) {
        return join(BusinessFlow::sadPath, BusinessFlow::happyPath, exception -> {
            try {
                peek.accept(exception);
                return BusinessFlow.failure(exception);
            } catch (Exception failure) {
                return BusinessFlow.failure(failure);
            }
        });
    }

    public Exception get() {
        return Optional.ofNullable(exceptionPath).orElseThrow(() -> new RuntimeException(format("Exception not present. Happy path was '%s'. Sad path was '%s'", happyPath, sadPath)));
    }
}
