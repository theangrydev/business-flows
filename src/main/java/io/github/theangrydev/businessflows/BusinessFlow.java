package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;
import com.codepoetics.ambivalence.RightProjection;

import java.util.Collections;
import java.util.List;

import static com.codepoetics.ambivalence.Eithers.split;
import static java.util.Arrays.stream;

public class BusinessFlow<Sad, Happy> extends BaseBusinessFlow<Sad, Happy> {

    BusinessFlow(Either<Sad, Happy> either, UncaughtExceptionHandler uncaughtExceptionHandler, TechnicalFailure<Sad> technicalFailure) {
        super(either, uncaughtExceptionHandler, technicalFailure);
    }

    @SafeVarargs
    public final ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        RightProjection<List<Sad>, Happy> attempt = either.left().map(Collections::singletonList).right().flatMap(happy -> validate(happy, validators));
        return new ValidationFlow<>(attempt, uncaughtExceptionHandler, technicalFailure);
    }

    private Either<List<Sad>, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>[] validators) {
        List<Sad> failures = stream(validators).map(validator -> attempt(validator, happy)).collect(split()).getLefts();
        if (failures.isEmpty()) {
            return Either.ofRight(happy);
        } else {
            return Either.ofLeft(failures);
        }
    }
}
