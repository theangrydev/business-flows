package io.github.theangrydev.businessflows;


import java.util.List;

public class ValidationFlow<Sad, Happy> extends BaseBusinessFlow<List<Sad>, Happy> {

    private ValidationFlow(List<Sad> sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> happyPath(Happy happy) {
        return new ValidationFlow<>(null, happy, null);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> sadPath(List<Sad> sad) {
        return new ValidationFlow<>(sad, null, null);
    }

    static <Sad, Happy> ValidationFlow<Sad, Happy> technicalFailure(Exception exception) {
        return new ValidationFlow<>(null, null, exception);
    }

    @SafeVarargs
    public final ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        BusinessFlow<List<Sad>, Happy> then = then(happy -> {
            ValidationFlow<Sad, Happy> validate = new BusinessFlow<Sad, Happy>(null, happy, null).validate(validators);
            return new BusinessFlow<>(validate.sadPath, validate.happyPath, validate.exceptionPath);
        });
        return new ValidationFlow<>(then.sadPath, then.happyPath, then.exceptionPath);
    }
}
