package io.github.theangrydev.businessflows;

import java.util.Optional;
import java.util.function.Consumer;

import static java.lang.String.format;

public class TechnicalFailure<Sad, Happy> extends BusinessFlowProjection<Sad, Happy>  {
    TechnicalFailure(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public BusinessFlow<Sad, Happy> peek(Consumer<Exception> peek) {
        return join(BusinessFlow::sadPath, BusinessFlow::happyPath, technicalFailure -> {
            try {
                peek.accept(technicalFailure);
                return BusinessFlow.technicalFailure(technicalFailure);
            } catch (Exception peekTechnicalFailure) {
                return BusinessFlow.technicalFailure(peekTechnicalFailure);
            }
        });
    }

    public Exception get() {
        return Optional.ofNullable(technicalFailure).orElseThrow(() -> new RuntimeException(format("Exception not present. Happy path was '%s'. Sad path was '%s'", happy, sad)));
    }
}
