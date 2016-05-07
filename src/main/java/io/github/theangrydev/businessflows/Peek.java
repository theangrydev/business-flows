package io.github.theangrydev.businessflows;

@FunctionalInterface
public interface Peek<T> {
    void peek(T instance) throws Exception;
}
