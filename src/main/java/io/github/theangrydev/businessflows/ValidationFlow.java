package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;

import java.util.Collections;
import java.util.List;

public class ValidationFlow<Sad, Happy> extends BaseBusinessFlow<List<Sad>, Happy> {
    private final TechnicalFailure<Sad> validationTechnicalFailure;

    ValidationFlow(Either<List<Sad>, Happy> either, UncaughtExceptionHandler uncaughtExceptionHandler, TechnicalFailure<Sad> technicalFailure) {
        super(either, uncaughtExceptionHandler, technicalFailure.andThen(Collections::singletonList));
        this.validationTechnicalFailure = technicalFailure;
    }

    @SafeVarargs
    public final ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return new ValidationFlow<>(then(happy -> new BusinessFlow<>(Either.ofRight(happy), uncaughtExceptionHandler, validationTechnicalFailure).validate(validators)).either, uncaughtExceptionHandler, validationTechnicalFailure);
    }
}
