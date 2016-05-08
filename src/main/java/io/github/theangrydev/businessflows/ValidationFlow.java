package io.github.theangrydev.businessflows;


import java.util.List;

public class ValidationFlow<Sad, Happy> extends BusinessFlow<List<Sad>, Happy> {

    private ValidationFlow(List<Sad> sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> happyPathValidation(Happy happy) {
        return new ValidationFlow<>(null, happy, null);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> sadPathValidation(List<Sad> sad) {
        return new ValidationFlow<>(sad, null, null);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> technicalFailureValidation(Exception exception) {
        return new ValidationFlow<>(null, null, exception);
    }

    @SafeVarargs
    public final ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        BusinessFlow<List<Sad>, Happy> then = then(happy -> {
            ValidationFlow<Sad, Happy> validate = HappyFlow.happyPath(happy).validate(validators);
            return new BusinessFlow<>(validate.sadPath, validate.happyPath, validate.exceptionPath);
        });
        return new ValidationFlow<>(then.sadPath, then.happyPath, then.exceptionPath);
    }
}
