package io.github.theangrydev.businessflows;

@FunctionalInterface
public interface HappyAttempt<Happy> {
    Happy happy() throws Exception;
}
