package io.github.theangrydev.businessflows;


import java.util.Arrays;
import java.util.List;

public class ValidationFlow<Sad, Happy> extends BusinessFlow<List<Sad>, Happy> {

    private ValidationFlow(List<Sad> sadPath, Happy happyPath, Exception technicalFailure) {
        super(sadPath, happyPath, technicalFailure);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> validationSuccess(Happy happy) {
        return new ValidationFlow<>(null, happy, null);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> validationFailed(List<Sad> sad) {
        return new ValidationFlow<>(sad, null, null);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> technicalFailureDuringValidation(Exception technicalFailure) {
        return new ValidationFlow<>(null, null, technicalFailure);
    }

    @SafeVarargs
    public final ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return validate(Arrays.asList(validators));
    }

    public ValidationFlow<Sad, Happy> validate(List<ActionThatMightFail<Sad, Happy>> validators) {
        BusinessFlow<List<Sad>, Happy> then = then(happy -> {
            ValidationFlow<Sad, Happy> validate = HappyFlow.happyPath(happy).validate(validators);
            return new BusinessFlow<>(validate.sad, validate.happy, validate.technicalFailure);
        });
        return new ValidationFlow<>(then.sad, then.happy, then.technicalFailure);
    }
}
