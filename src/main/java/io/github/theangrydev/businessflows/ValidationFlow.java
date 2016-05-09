package io.github.theangrydev.businessflows;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationFlow<Sad, Happy> extends BusinessFlow<List<Sad>, Happy> {

    private ValidationFlow(List<Sad> sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    private static <Sad, Happy> ValidationFlow<Sad, Happy> validationSuccess(Happy happy) {
        return new ValidationFlow<>(null, happy, null);
    }

    private static <Sad, Happy> ValidationFlow<Sad, Happy> validationFailed(List<Sad> sad) {
        return new ValidationFlow<>(sad, null, null);
    }

    private static <Sad, Happy> ValidationFlow<Sad, Happy> technicalFailureDuringValidation(Exception technicalFailure) {
        return new ValidationFlow<>(null, null, technicalFailure);
    }

    public static <Sad, Happy> ValidationFlow<Sad, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>... validators) {
        return validate(happy, Arrays.asList(validators));
    }

    public static <Sad, Happy> ValidationFlow<Sad, Happy> validate(Happy happy, List<ActionThatMightFail<Sad, Happy>> validators) {
        List<Sad> validationFailures = new ArrayList<>(validators.size());
        for (ActionThatMightFail<Sad, Happy> validator : validators) {
            try {
                validator.attempt(happy).ifPresent(validationFailures::add);
            } catch (Exception technicalFailure) {
                return technicalFailureDuringValidation(technicalFailure);
            }
        }
        if (validationFailures.isEmpty()) {
            return validationSuccess(happy);
        } else {
            return validationFailed(validationFailures);
        }
    }

    @SafeVarargs
    public final ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return validate(Arrays.asList(validators));
    }

    public ValidationFlow<Sad, Happy> validate(List<ActionThatMightFail<Sad, Happy>> validators) {
        return join(ValidationFlow::validationFailed, happy -> validate(happy, validators), ValidationFlow::technicalFailureDuringValidation);
    }
}
