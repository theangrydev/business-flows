package io.github.theangrydev.businessflows;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationPath<Sad, Happy> extends HappyPath<List<Sad>, Happy> {

    protected ValidationPath(List<Sad> sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    public static <Sad, Happy> ValidationPath<Sad, Happy> validationSuccess(Happy happy) {
        return new ValidationPath<>(null, happy, null);
    }

    public static <Sad, Happy> ValidationPath<Sad, Happy> validationFailed(List<Sad> sad) {
        return new ValidationPath<>(sad, null, null);
    }

    public static <Sad, Happy> ValidationPath<Sad, Happy> technicalFailureDuringValidation(Exception technicalFailure) {
        return new ValidationPath<>(null, null, technicalFailure);
    }

    @SafeVarargs
    public static <Sad, Happy> ValidationPath<Sad, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>... validators) {
        return validate(happy, Arrays.asList(validators));
    }

    public static <Sad, Happy> ValidationPath<Sad, Happy> validate(Happy happy, List<ActionThatMightFail<Sad, Happy>> validators) {
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
    public final ValidationPath<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return validate(Arrays.asList(validators));
    }

    public ValidationPath<Sad, Happy> validate(List<ActionThatMightFail<Sad, Happy>> validators) {
        return join(ValidationPath::validationFailed, happy -> validate(happy, validators), ValidationPath::technicalFailureDuringValidation);
    }
}
