package io.github.theangrydev.businessflows;

import com.codepoetics.ambivalence.Either;

import static com.codepoetics.ambivalence.Either.ofRight;

public class BusinessFlows {

    private final UncaughtExceptionHandler uncaughtExceptionHandler;

    public BusinessFlows(UncaughtExceptionHandler uncaughtExceptionHandler) {
        this.uncaughtExceptionHandler = uncaughtExceptionHandler;
    }

    public <Happy> BusinessFlow<Exception, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt) {
        return happyAttempt(happyAttempt, exception -> exception);
    }

    public <Sad, Happy> BusinessFlow<Sad, Happy> happyAttempt(HappyAttempt<Happy> happyAttempt, TechnicalFailure<Sad> technicalFailure) {
        try {
            return happyPath(happyAttempt.happy(), technicalFailure);
        } catch (Exception sad) {
            return sadPath(technicalFailure.wrap(sad), technicalFailure);
        }
    }

    public <Sad, Happy > BusinessFlow<Sad, Happy> happyPath(Happy happy, TechnicalFailure<Sad> technicalFailure) {
        return new BusinessFlow<>(ofRight(happy), uncaughtExceptionHandler, technicalFailure);
    }

    public <Sad, Happy > BusinessFlow<Sad, Happy> sadPath(Sad sad, TechnicalFailure<Sad> technicalFailure) {
        return new BusinessFlow<>(Either.ofLeft(sad), uncaughtExceptionHandler, technicalFailure);
    }
}
