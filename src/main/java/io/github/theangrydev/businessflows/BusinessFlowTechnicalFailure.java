package io.github.theangrydev.businessflows;

public class BusinessFlowTechnicalFailure<Sad, Happy> extends Projection<Sad, Happy> {

    BusinessFlowTechnicalFailure(Sad sadPath, Happy happyPath, Exception exceptionPath) {
        super(sadPath, happyPath, exceptionPath);
    }

    public Exception get() {
        return exceptionPath;
    }
}
