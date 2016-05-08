package io.github.theangrydev.businessflows;


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
}
