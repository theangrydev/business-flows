package io.github.theangrydev.businessflows;

import java.util.Optional;

import static java.lang.String.format;

public class TechnicalFailure<Sad, Happy> extends BusinessFlowProjection<Sad, Happy>  {
    TechnicalFailure(Sad sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    private BusinessFlow<Sad, Happy> then(Mapping<Exception, BusinessFlow<Sad, Happy>> action) {
        return join(BusinessFlow::sadPath, BusinessFlow::happyPath, technicalFailure -> {
            try {
                return action.map(technicalFailure);
            } catch (Exception technicalFailureDuringAction) {
                return BusinessFlow.technicalFailure(technicalFailureDuringAction);
            }
        });
    }

    public BusinessFlow<Sad, Happy> map(Mapping<Exception, Exception> mapping) {
        return then(mapping.andThen(BusinessFlow::technicalFailure));
    }

    public BusinessFlow<Sad, Happy> peek(Peek<Exception> peek) {
        return then(technicalFailure -> {
            peek.peek(technicalFailure);
            return BusinessFlow.technicalFailure(technicalFailure);
        });
    }

    public Exception get() {
        return Optional.ofNullable(technicalFailure).orElseThrow(() -> new RuntimeException(format("Exception not present. Happy path was '%s'. Sad path was '%s'", happy, sad)));
    }
}
