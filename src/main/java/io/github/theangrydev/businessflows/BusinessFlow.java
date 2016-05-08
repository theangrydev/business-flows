package io.github.theangrydev.businessflows;


import java.util.ArrayList;
import java.util.List;

public class BusinessFlow<Sad, Happy> extends BaseBusinessFlow<Sad, Happy> {

    BusinessFlow(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        try {
            return happyPath(happyAttempt.happy());
        } catch (Exception exception) {
            return technicalFailure(exception);
        }
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> happyPath(Happy happy) {
        return new BusinessFlow<>(null, happy, null);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> sadPath(Sad sad) {
        return new BusinessFlow<>(sad, null, null);
    }

    public static <Sad, Happy> BusinessFlow<Sad, Happy> technicalFailure(Exception exception) {
        return new BusinessFlow<>(null, null, exception);
    }

    @SafeVarargs
    public final ValidationFlow<Sad, Happy> validate(ActionThatMightFail<Sad, Happy>... validators) {
        return join(
                sadPath -> ValidationFlow.technicalFailure(new IllegalStateException(sadPath.toString())), //TODO: what should the behaviour be here??
                happy -> validate(happy, validators),
                ValidationFlow::technicalFailure);
    }

    private ValidationFlow<Sad, Happy> validate(Happy happy, ActionThatMightFail<Sad, Happy>[] validators) {
        List<Sad> failures = new ArrayList<>();
        for (ActionThatMightFail<Sad, Happy> validator : validators) {
            BusinessFlow<Sad, Happy> attempt =  attempt(validator).join(BusinessFlow::sadPath, BusinessFlow::happyPath, BusinessFlow::technicalFailure);
            if (attempt.exceptionPath != null) {
                return ValidationFlow.technicalFailure(attempt.exceptionPath);
            }
            attempt.sadPath().peek(failures::add);
        }
        if (failures.isEmpty()) {
            return ValidationFlow.happyPath(happy);
        } else {
            return ValidationFlow.sadPath(failures);
        }
    }
}
