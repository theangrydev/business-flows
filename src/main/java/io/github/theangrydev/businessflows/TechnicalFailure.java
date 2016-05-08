package io.github.theangrydev.businessflows;

import java.util.function.Function;

public class TechnicalFailure<Sad, Happy> extends Projection<Sad, Happy> {

    TechnicalFailure(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    public Exception get() {
        return exceptionPath;
    }
}
