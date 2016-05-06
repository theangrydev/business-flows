package io.github.theangrydev.businessflows;

@FunctionalInterface
public interface UncaughtExceptionHandler {
    void handle(Exception exception);
}
